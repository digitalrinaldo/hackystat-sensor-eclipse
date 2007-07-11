package org.hackystat.sensor.eclipse.addon;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Implements a meter to measure several aspects such as number of methods, statements, test 
 * methods, assertion statements if applicable. Number of test methods, assertion statements is
 * reported to assist Test-Driven Development study.
 * 
 * @author Hongbing Kou
 * @version $Id: JavaStatementMeter.java,v 1.1.1.1 2005/10/20 23:56:56 johnson Exp $
 */
public class JavaStatementMeter extends ASTVisitor {
  /** Name of this compilation unit. */
  private String name; 
  
  /** Number of methods. */
  private int numOfMethods = 0;
  /** Number of invocations. */
  private int numOfStatements = 0;
  
  /** Saves number of tests if there is any. */
  private int numOfTestMethods = 0;
  /** Saves number of test assertions in this class if there is any. */
  private int numOfTestAssertions = 0;
  
  /**
   * Visit class type declaration to get name of this object.
   * 
   * @param td TypeDeclaration such as class definition.
   * @return True if continute to check child nodes. 
   */
  public boolean visit(TypeDeclaration td) {
    if (td.getName() != null) {
      this.name = td.getName().getIdentifier();
    }
    
    return true;
  }

  
  /**
   * Checks whether the given method is a JUnit 4 unit test.
   * 
   * @param md Method declaration.
   * @return True if it is a unit test of JUnit 4. 
   */
  private boolean isJUnit4Test(MethodDeclaration md) {
    List modifiers = md.modifiers();
    for (Iterator i = modifiers.iterator(); i.hasNext();) {
      IExtendedModifier modifer = (IExtendedModifier) i.next();
      if (modifer.isAnnotation()) {
        Annotation annotation = (Annotation) modifer;
        if ("Test".equals(annotation.getTypeName().getFullyQualifiedName())) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  
  /**
   * Visits the method invocation to get number of assertion in this test method.
   * 
   * @param md Visit method declaration in source code.
   * @return False because we don't look into child nodes. 
   */
  public boolean visit(MethodDeclaration md) {
    if (md.getName() != null) {
      this.numOfMethods++;
      
      if (md.getName().getIdentifier().startsWith("test")) {
        this.numOfTestMethods++;
      }
      else if (isJUnit4Test(md)) {
        this.numOfTestMethods++;
      }
      
      // Check test method body to look for assertion statement. 
      Block methodBody = md.getBody();
      if (methodBody != null && methodBody.statements() != null) {
        List stmts = methodBody.statements();
        this.numOfStatements += stmts.size();
        // Looks through all statements in this method body.
        for (Iterator i = stmts.iterator(); i.hasNext();) {
          Statement stmt = (Statement) i.next();
          // MethodInvocation is one kind of expression statement.
          if (stmt instanceof ExpressionStatement) {
            ExpressionStatement estmt = (ExpressionStatement) stmt;
            if (estmt.getExpression() instanceof MethodInvocation) {
              MethodInvocation mi = (MethodInvocation) estmt.getExpression();
              // Increment number of test assertions.
              if (mi.getName() != null && mi.getName().getIdentifier().startsWith("assert")) {
                this.numOfTestAssertions++;
              }
            }
          }
        }
      }
    }
        
    //No need to visit child nodes anymore.
    return false;
  }
  
  /**
   * Gets number of methods.
   * 
   * @return Number of methods.
   */
  public int getNumOfMethods() {
    return this.numOfMethods;
  }
  
  /**
   * Gets number of statemetns.
   * 
   * @return Number of statements.
   */
  public int getNumOfStatements() {
    return this.numOfStatements;
  }
  
  /**
   * Gets number of tests.
   * 
   * @return Number of test.
   */
  public int getNumOfTestMethods() {
    return this.numOfTestMethods;
  }
  
  /**
   * Gets number of test assertion statements.
   * 
   * @return Number of assertion statements
   */
  public int getNumOfTestAssertions() {
    return this.numOfTestAssertions;
  }
  
  /**
   * Check whether this compilation unit has test. 
   * 
   * @return True if it has any test method or assertion.
   */
  public boolean hasTest() {
    return this.numOfTestMethods > 0 || this.numOfTestAssertions > 0;   
  }
  
  /**
   * Returns meter values in string.
   * 
   * @return Metrics value string. 
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("*****  " + this.name + "   *****\n");
    buf.append("Methods     : " + this.numOfMethods + "\n");
    buf.append("Statements  : " + this.numOfStatements);
        
    // Appends test info if there is any.
    if (this.hasTest()) {
       buf.append("\nTests       : " + this.numOfTestMethods);
       buf.append("\nAssertions  : " + this.numOfTestAssertions);
    }
    
    return buf.toString();
  }
}