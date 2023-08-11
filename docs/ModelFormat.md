# Model Format

Concoction uses JSON to model what should be searched for during scanning. The model follows this outline:

```json
{
  "archetype": {
    "level": "STRONG",
    "identifier": "Windows Registry",
    "description": "Indicators of working with Windows Registry. Its very rare to see this with good intentions in Java."
  },
  "code-patterns": {
    "key": [ { ... }, { ... } ]
  },
  "code-behaviors": {
    "key": { ... }
  }
}
```

## Archetype

The archetype section of each model file outlines what the model is supposed to be targeting. 
In this example, the model will be searching for things that indicate usage of the Windows Registry.
While some native applications on Windows use this to store preferences, it is exceptionally rare
to see Java applications doing this given the multi-platform focus of the language.

This fact is mentioned in the description section.

The threat/suspicion/risk levels are also declared. The possible levels are:

- `MAXIMUM`: Almost guaranteed to be malicious.
- `STRONG`: Very likely to be malicious, but in some cases it may just be benign shoddy code.
- `MEDIUM`: Typically these are strictly context based. For instance, deleting a file is usually not a concern. Deleting core operating system files is a concern though.
- `WEAK`: Unlikely to be malicious in most circumstances.
- `NOTHING_BURGER`: Incredibly unlikely to be malicious in any circumstance.

Given that the Windows Registry can be used to make a program launch at startup the risk factor
is marked as `STRONG`. 

## Code Patterns

The `code-patterns` section of each model is a map where each entry is a `name --> pattern-list`.
The `pattern-list` describes a sequence of JVM instruction matchers. This means that if you define a list with
five entries, it will match five sequential instructions _(Unless you use wildcards, but more on that later)._

In Java class files, data is stored at the beginning of the file and instructions reference offsets that point
to this data. To make matching easier, we represent things as if all the data is inline and in a text format.
To illustrate this, lets outline what the potential instruction matchers are.

### Instruction

Matching an instruction has two parts:

- Matching the instruction opcode by name
- Matching the instruction arguments by text representation

The text-match modes supported are:

- `EQUALS`: Content must be an exact match.
- `EQUALS_IC`: Content must be an exact match, but case of letters (`A` vs `a`) is ignored.
- `CONTAINS`: Content must contain some text.
- `CONTAINS_IC`: Content must contain some text, but case of letters (`A` vs `a`) is ignored.
- `STARTS_WITH`: Content must start with some text.
- `STARTS_WITH_IC`: Content must start with some text, but case of letters (`A` vs `a`) is ignored.
- `ENDS_WITH`: Content must end with some text.
- `ENDS_WITH_IC`: Content must end with some text, but case of letters (`A` vs `a`) is ignored.
- `REGEX_FULL_MATCH`: Content must be a complete match for a given regex pattern.
- `REGEX_PARTIAL_MATCH`: Content must be at least a partial match for a given regex pattern.
- `ANYTHING`: Content is matched regardless of what is declared.
- `NOTHING`: Content is never matched regardless of what is declared.

The format is this matcher is as follows:
```json
{
  "op": "<text-match-mode> <instruction-name>",
  "args": "<text-match-mode> <instruction-args-as-text>"
}
```
An example of matching a call to `Runtime.exec(String)` would look like:
```json
{
  "op": "EQUALS invokevirtual",
  "args": "EQUALS java/lang/Runtime.exec(Ljava/lang/String;)Ljava/lang/Process;"
}
```
The `exec` method is an instance method, so it will likely be invoked with `invokevirtual`, however `invokespecial`
would also be valid. Given that, you could alternatively use `STARTS_WITH invoke` to cover both bases.

The argument for `invoke` instructions is the signature of the method.
We represent that as `<owner-class>.<name><desc>`.

- The `owner-class` is the name of the class defining the method.
- The `name` is the name of the method.
- The `desc` is the type descriptor of the method. This follows the [descriptor format described in the JVMS spec](https://docs.oracle.com/javase/specs/jvms/se20/html/jvms-4.html#jvms-4.3).

### Single Wildcard

Simply a string containing `*` will indicate this value will match _any_ instruction once.

### Multi-match Wildcard

A string containing `**` will match _any_ instruction any amount of times.

A string containing `*N` will match _any_ instruction `N` times. So `*3` will match up to three instructions in a row.

These multi-match wildcards will continue to match sequential instructions up until 
one matches the next matcher in the `pattern-list`.

### Alternatives

Say you have a `pattern-list` with three instructions you want to match, but the second instruction can be a number 
of different things. You could use a wildcard here, but let's say you want to be more specific than that.
This is where alternatives come in. You can replace _one_ instruction matcher with multiple.

Here's an example:
```json
{
  "code-patterns": {
    "example": [
      { "op": "EQUALS foo", "args": "EQUALS bar" },
      { "ANY": [
         { "op": "EQUALS can-be", "args": "EQUALS this-one" },
         { "op": "EQUALS or", "args": "EQUALS this-one" }
      ]},
      { "op": "EQUALS foo", "args": "EQUALS bar" }
    ]
  }
}
```

The options for alternatives are:
- `ANY`: Any of the provided matchers can match.
- `ALL`: All of the provided matchers must match.
- `NONE`: None of the provided matchers must match.

The use case for `ANY` is what we covered in the example above.

The use case for `ALL` is mostly for when you're using regex text match modes, and would like to ensure they all overlap.
For example, you could do a partial regex match for an ip-address, and another match for ending with a `.exe` file.
This would represent downloading a `.exe` file from a website accessed via direct ip-address. 

The use case for `NONE` is mostly for blacklisting.
For example, lets say there is some class `SussyApi` that defines ten methods. Eight of them malicious, but two are not.
Rather than define matches for all malicious cases, you can instead match against the two benign cases and invert the 
match using `NONE`.

## Code Behaviors

The `code-behaviors` section of each model is a map where each entry is a `name --> matcher`.
The `matcher` will match against the current state of a simulated execution of the program being scanned.

A `matcher` can either represent a single conditional check against the program state, or a list of checks similar 
to the _"alternatives"_ described in the `code-patterns` section above.

### Single condition

The format for single condition matchers is:
```json
 {
  "location": {
    "class": "<text-match-mode> <class-name>",
    "mname": "<text-match-mode> <method-name>",
    "mdesc": "<text-match-mode> <method-desc>"
  },
  "condition": { ... }
}
```
The `location` describes where in the program this condition applies to.
Each part of the location is optional. If you do not include it, any value for that part will match.
For example, if your location only includes `{ "class": "EQUALS java/lang/Runtime" }` then the `condition` will
apply to _any_ method within the `Runtime` class.

The `condition` describes something in the program execution we want to match against.
The list of conditions are:

#### Text value checking

Using the given format you can check parameter/variables for different text comparisons:
```json
{
  "index": 1,
  "extraction": "<extraction-mode>",
  "match": "<text-match-mode> <value>"
}
```
The `extraction-mode` values are:
- `known-types`: Only types implementing `CharSequence` like `String`/`StringBuilder`/`StringBuffer` are checked.
- `any-type`: All object values are checked against their `toString()` value

Additionally, the `extraction-mode` is optional. Not including it defaults to `known-types`.

The `match` follows the same format of other similar components. The text match mode, then the text to match with after.

#### Numeric value checking

Using the given format you can check parameter/variables for different numeric comparisons:
```json
{
  "index": 1,
  "match": "<operation> <value>"
}
```
The `value` is any number that can be parsed with [the default `NumberFormat` instance](https://docs.oracle.com/javase/8/docs/api/java/text/NumberFormat.html).

The `operation` options are:

- `==` Equals
- `!=` Not equals
- `>` Greater than
- `>=` Greater or equal than
- `<` Less than
- `<=` Less or equal than
- `&` Has bitwise mask of
- `%` Is divisible by

#### Null checking

Using the given format you can check parameter/variables for being `null` and not `null`
```json
{
  "index": 1,
  "null": true
}
```

#### Any

Using the string `ANY` will match under any circumstance. You can use this to register any case where the `location`
section matches the current location in the simulated program execution.

#### None

Using the string `NONE` will never match anything. As described with similar usages of inversions, this is primarily
used in blacklist cases.

### Alternatives

It's the same thing as before with `code-patterns`, but instead with the contents being `matcher` items.

Using `ANY` you can say _"any one of these conditions can match to mark the current location in execution as a match"_.

Using `ALL` allows you to say _"all of these conditions must match to mark the current location in execution as a match"_.

Using `NONE` allows you to say _"none of these conditions can match if to mark the current location as a match"_.
To effectively use `NONE` you will want to combine it with `ALL`.
An example of which where we want to match all usages of `MaliciousApi` except the method `exceptThisOneOkMethod` would look like:
```json
{
  "ALL": [
    {
      "location": { "class": "EQUALS com/example/MaliciousApi" },
      "condition": "ANY"
    },
    {
      "NONE": [{
        "location": { "class": "EQUALS com/example/MaliciousApi", "mname": "exceptThisOneOkMethod" },
        "condition": "ANY"
      }]
    }
  ]
}
```