# CalculatorApp

## General Information
## Features
- Can evaluate expressions containing positive/negative integers/floats using infix and postfix algorithms
- Has responsive UI
- Customizable Colors: Highlighting, Function/Operator/Number Buttons, as well as input and output font colors
- Supports standard calculator operations
- Supports advanced calculator operations (squared, square root, natural logarith, logarithm base 10, factorial (max number is limited to 50))
- Supports removal of operators and operands anywhere in the expression
- Supports editing of operators and operands anywhere in the expression
- Supports editing of similar functions (lg, log, square root)
- Dynamically evaluates expression, saves expression result on the Equal button press
- Supports Day and Night Mode

This program allows user to calculate standard mathematical expressions (as any other basic calculator)

## User Guide

#### Editing Feature
User can edit their expression at anytime by entering "Edit Mode". To enter Edit Mode, press on any operator or operand of the expression. The editable operator/operand will be highlighted for convenience:

![EditingMode 480x](https://user-images.githubusercontent.com/38502074/169933781-c7b0ec76-8dd4-4f4f-98d7-fa296d11ee24.gif)


To exit Edit Mode, press "equal button" (which will be displayed as "check mark" in Edit Mode):

![CheckButton 480x](https://user-images.githubusercontent.com/38502074/169933795-b84bf958-6e8d-45fd-ae1d-858f04c77564.gif)


While in Edit Mode, user can freely choose any other operand/operator and change them accordingly:

![FreeEditing 480x](https://user-images.githubusercontent.com/38502074/169933811-23961f46-3853-43e6-ad05-fceddc2aadfc.gif)


Edit Mode has type safety, which means operators can only be replaced by other operators, and numbers can only be replaced by other numbers. All other buttons, are turned off for safety. 


#### Negative Numbers
Negative numbers are fully supported:

![NegativeNumbers 480x](https://user-images.githubusercontent.com/38502074/170172132-c252b956-3263-4182-956a-601c91a1f12f.gif)


There also exist typesafety... Changing "number" sign on anything other than "-" will result in "NaN" result:

![TypeSafety 480x](https://user-images.githubusercontent.com/38502074/169934111-9cb4a0dc-1f3a-4d06-93d5-56aa736b8f60.gif)


#### Advanced Mode
Calculator also supports "advanced mode". It can be accessed by pressing "change layout" button. In advanced mode, user can use additional mathematical functions, as well as parentheses.

![AdvancedMode 480x](https://user-images.githubusercontent.com/38502074/169934294-56440a03-ead7-4876-a32a-a1dfc441bceb.gif)



