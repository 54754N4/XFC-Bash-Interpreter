# XFC Bash Interpreter
 Cross-functional commands Bash interpreter

## Input Handling

#### Completed:
```
+---- Brace expansion
|    +---- Comma separated sequence                   {a,b,z}
|    +---- Integer and char ranges (including step)   {0..9..2}
|    +---- Combined expressions                       {A-Z}{a-z}
|    +---- Nested expressions                         {{a..z},{0..9}}
+---- Parameter and variable expansion
|    +---- Simple usage                               $param
|    +---- Indirection                                ${!param}
|    +---- Case modification                          ${param^^}
|    +---- Variable name expansion                    ${!param*}
|    +---- Substring removal                          ${param#pattern}
|    +---- Search and replace                         ${param//pattern/replacement}
|    +---- String length                              ${#param}
|    +---- Substring expansion                        ${param:offset:length}
|    +---- Use default value                          ${param:-value}
|    +---- Assign default value                       ${param:=value}
|    +---- Use alternate value                        ${param:+value}
|    +---- Display error if null or unset             ${param:?value}
+---- Arithmetic expansion
```
#### Incomplete:
```
+---- Tilde expansion
+---- Pathname expansion
+---- Command substitution
+---- Process substitution
```

## Terminal

#### Complete:
```
+---- Native commands
+---- Custom commands
+---- I/O redirection and piping
+---- Native and custom command interoperability
```

#### Incomplete:
```
+---- Bash 
|    +---- Grammar
|    +---- Lexer
|    +---- Parser
|    +---- Interpreter
|    +---- AST
+---- Including arrays as a valid variable type (parameter expansion)
+---- Android launcher
|    +---- Giving commands access to android context pre-execution
|    +---- Creating default set of custom android commands
|    +---- Modular fragments/activities (e.g. lives inside launcher)
|         +---- File editor
|         +---- Music player
|         +---- Browser
|    +---- Seamless working directory handling
```
