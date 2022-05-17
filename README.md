# CalculatorApp

## General Information
## Features
- Can evaluate expressions containing positive/negative integers/floats using infix and postfix algorithms
- Has responsive UI (colors and input/output formats will be customizable in the feature)
- Supports standard calculator operations (support of functions (e.g. log, ln, cos, sin) are in progress)
- Supports removal of operators and operands anywhere in the expression
- Supports editing of operators and operands anywhere in the expression
- Dynamically evaluates expression, saves expression result on the Equal button press

This program allows user to calculate standard mathematical expressions (as any other basic calculator)

## User Guide

#### Editing Feature
User can edit their expression at anytime by entering "Edit Mode". To enter Edit Mode, press on any operator or operand of the expression. The editable operator/operand will be highlighted for convenience:

![Edit Mode 480x](https://user-images.githubusercontent.com/38502074/168490142-7a79b457-643f-422e-b40d-80a688ae7c1d.gif)


To exit Edit Mode, press "equal button" (which will be displayed as "check mark" in Edit Mode):

![Check Mark 480x](https://user-images.githubusercontent.com/38502074/168490139-551bea81-c349-415b-ae77-df1787858f77.gif)


While in Edit Mode, user can freely choose any other operand/operator and change them accordingly:

![Different Tokens 480x](https://user-images.githubusercontent.com/38502074/168490135-135269b0-f173-4652-8ffe-ac0dd3c0d7e9.gif)

Edit Mode has type safety, which means operators can only be replaced by other operators, and numbers can only be replaced by other numbers. All other buttons, are turned off for safety. 


#### Negative Numbers
Currently only "first-in-line" negative numbers are supported. That means, user can only enter negative number if it's the first number in an expression:

![Negative Numbers 480x](https://user-images.githubusercontent.com/38502074/168862259-b1a9d4e6-6234-4569-9231-7f384d60f1a4.gif)

There also exist typesafety... Changing "number" sign on anything other than "-" will result in "Infinity" result:

![Negative Numbers TypeSafety 480x](https://user-images.githubusercontent.com/38502074/168862471-da75bbc8-f4ba-4e90-ac9e-91aa51fdb285.gif)
