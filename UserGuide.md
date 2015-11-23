The InstallationGuide provides instructions on how to install and configure the Hackystat Eclipse sensor.

After that is accomplished, there is not much to do to "use" the sensor: it collects data automatically and sends it to the server.

Here is a table indicating the kinds of data this is collected by this sensor.

| **Tool**   | **SensorDataType** | **Type** | **Subtype** | Description |
|:-----------|:-------------------|:---------|:------------|:------------|
|Eclipse     | DevEvent           | Edit     | StateChange | Generated when the active buffer has changed.|
|Eclipse     |DevEvent            | Edit     | OpenFile    | Generated when a new file is opened.|
|Eclipse     |DevEvent            | Edit     | SaveFile    | Generated when the current file is saved.|
|Eclipse     |DevEvent            | Edit     | CloseFile   | Generated when the current file is closed.|
|Eclipse     |DevEvent            | Edit     | Exit        | Generated when Eclipse is exited.|
|Eclipse     |DevEvent            | Edit     | ProgramUnit(Add) | Generated when adding a new program construct such as an import statement, class, field, or method.|
|Eclipse     |DevEvent            | Edit     | ProgramUnit(Rename) | Generated when renaming a program construct.|
|Eclipse     |DevEvent            | Edit     | ProgramUnit(Remove) | Generated when removing a program construct.|
|Eclipse     |DevEvent            | Edit     | ProgramUnit(Move) | Generated when moving a program construct from one location to another.|
|Eclipse     |DevEvent            | Build    | Compile     | Generated when build fails.|
|Eclipse     |DevEvent            | Debug    | Breakpoint(Set/Unset) | Generated when a breakpoint is set/unset.|
|Eclipse     |DevEvent            | Debug    | Start       | Generated when starting a debugging session.|
|Eclipse     |DevEvent            | Debug    | StepInto    | Generated when stepping into a code block.|
|Eclipse     |DevEvent            | Debug    | StepOver    | Generated when stepping over a code block.|
|Eclipse     |DevEvent            | Debug    | Terminate   | Generated when terminating a debugging session.|

The Resource property of a Sensor Data entry is the file attached to the active buffer.