# Cats Error Handling Workshop

Copyright 2021 Dave Gurnell.

Licensed [Apache 2](http://www.apache.org/licenses/LICENSE-2.0.html).

## Adding a Bounding Box

Extend the search and count commands so they take two extra parameters
representing a bounding box:

```
sbt run search <layerId> <swGps> <neGps>
```

`swGps` and `neGps` should be GPS positions specified as `x,y` positions.
For example, the following would query a box centered roughly on London:

```
sbt run search morph -1,49 1,51
```

If either GPS position is invalid, fail with an invormative error message.

### Tips

Do the exercise in several steps:

**Step 1**

Start by creating a method `parsePoint` to parse an `x,y` string as a `Point`:

```scala
def parsePoint(string: String): Either[String, Point] =
  ???
```

You may find the following snippets of code useful:

```scala
// Split a String to a list of substrings:
string.split(",").toList // => List[String]

// Safely convert a String to a Double:
string.toDoubleOption.toRight("Error") // => Either[String, Double]

// Create a Point from an x and a y:
Point(double, double)
```

You'll need to find a way to combine the `Either[String, Double]`
values to create your point. You can either use a `for` comprehension
or look to Cats for a more convenient method.

**Step 2**

Now create a second method, `parseBounds`, that takes two GPS strings
as parameters. The method should call `parsePoint` twice, once for each corner,
and combines the results to create a `Box`:

```scala
/**
 * The first parameter represents the South West (bottom left) corner;
 * The second parameter represents the North East (top right) corner.
 */
def parseBounds(sw: String, ne: String): Either[String, Box] =
  ???
```

The code to combine the `Either[String, Point]` values in `parseBounds`
will be similar to the code to combine `Either[String, Double]` values in `parsePoint`.

**Step 3**

Once you have `parsePoint` and `parseBounds`,
add additional parameters to `search` and `count`
to receive the parsed `Points`:

```scala
def search(layerId: String, sw: String, ne: String): Either[String, String] =
  ???

def count(layerId: String, sw: String, ne: String): Either[String, String] =
  ???
```

In each case create a `Box` from the new strings and pass it as an additional parameter to `MapApi.query`.

**Step 4**

Finally, modify the pattern match in `main`
to read and pass in the extra parameters:

```scala
def main(args: Array[String]): Unit =
  printOutput {
    args.toList match {
      case "search" :: layerId :: sw :: ne :: Nil =>
        search(layerId, sw, ne)

      case "count"  :: layerId :: sw :: ne :: Nil =>
        count(layerId, sw, ne)

      case _ =>
        Left("Wrong number of parameters")
    }
  }
```

## Make the Bounds Parameters Optional

Modify your `main` method so it can parse bounds and no-bounds
variants of each command:

```scala
def main(args: Array[String]): Unit =
  printOutput {
    args.toList match {
      case "search" :: layerId :: Nil =>
        search(layerId, None, None)
      case "count"  :: layerId :: Nil =>
        count(layerId, None, None)

      case "search" :: layerId :: sw :: ne :: Nil =>
        search(layerId, Some(sw), Some(ne))
      case "count"  :: layerId :: sw :: ne :: Nil =>
        count(layerId, Some(sw), Some(ne))

      case _ =>
        Left("Wrong number of parameters")
    }
  }
```

Modify your definitions of `search` and `count` to make the bounds parameters `Options`:

```scala
def search(layerId: String, sw: Option[String], ne: Option[String]): Either[String, String] =
  ???

def count(layerId: String, sw: Option[String], ne: Option[String]): Either[String, String] =
  ???
```

Chase through the changes to the types.
Find the minimum code you need to fix the type errors.
Methods from Cats will come in exceptionally useful here!

## Total and Average Commands

Implement two new commands:

- `sbt run total <layerId> <propId> [sw] [ne]`
- `sbt run average <layerId> <propId> [sw] [ne]`

These should select a named property from the queried dataset
and calculate the total/average value across the results.

Your code should assume that the specified property is numeric.
It should fail if:

- the layer is not found;
- the property is not found;
- the property could not be converted to the correct type.

### Tips

Here are some stubs for the methods in `Main.scala`.
Place these along-side the definitions of `search` and `count`:

```scala

def total(layerId: String, propId: String,
    sw: Option[String], ne: Option[String]): Either[String, String] =
  ???

def average(layerId: String, propId: String,
    sw: Option[String], ne: Option[String]): Either[String, String] =
  ???
```

In each case, start by querying the API to get a `Vector[Feature]`.
After that try to calculate your total/average of type `Double`.
Aim to perform your calculation with the following steps:

1. Fetch relevant property from each `Feature`,
   giving you a `Vector[Either[String, Double]]`.

2. Use methods from Cats to convert the `Vector[Either[String, Double]]`
   to an `Either[String, Vector[Double]]`.

3. Add the numbers in the `Vector[Double]` to create a single `Double`.

You may find the following methods of `Feature` useful for step 1:

```scala
class Feature {
  /** Fetch the value of the specified property.
   * Fail if the property was not found.
   */
  def prop(name: String): Either[String, JsValue] =
    ???

  /** Fetch the value of the specified property and convert to type `A`.
   * Fail if the property wqs not found or the value could not be converted.
   */
  def propAs[A: Reads](name: String): Either[String, A] =
    ???
}
```

## Cumulative Error Reporting (Optional)

Modify your code so that, instead of reporting the first error to go wrong, it reports all the errors it can at any one time:

- If it can't parse the command line arguments, report all parsing errors.
- If it can't sum/average the query results, report all result parsing errors.

### Tips

**Step 1**

Start by modifying the `printOutput` method to expect a list of error messages:

```scala
def printOutput(output: Either[List[String], String]): Unit =
  ???
```

Chase the compilation errors through your code, changing all errors to lists.

**Step 2**

Look for places where you can swap fail-fast error handling
for cumulative error handling:

- calls to `mapN` can be replaced with `parMapN`;
- calls to `tupled` can be replaced with `parTupled`;
- calls to `sequence` can be replaced with `parSequence`;
- calls to `traverse` can be replaced with `parTraverse`.

Also look for places where you have written `for` comprehensions
where all the generators are independent of one another.
These can be converted to calls to `mapN`, which can in turn be replaced with `parMapN`:

```scala
// As long as `expr1` and `expr2` don't refer to one another,
// this for comprehension can be rewritten using `mapN`:

for {
  x <- expr1
  y <- expr2
} yield x + y

// The equivalent call to mapN is as follows:

(expr1, expr2).parMapN(_ + _)
```

**Step 3**

It's common to use a non-empty sequence type on the left of an `Either`
to avoid accidentally returning an empty list of errors.

Cats provides threww non-empty sequence types.
Two are thin wrappers around counterpart from the standard libraryl
the third wraps `cats.data.Chain`, a data type with fast prepend and append operations:

```scala
// Use any of these:
import cats.data.NonEmptyList   // a thin wrapper around List
import cats.data.NonEmptyVector // a thin wrapper around Vector
import cats.data.NonEmptyChain  // a thin wrapper around cats.data.Chain
```

Convert your code to use one of these sequence types to hold errors:

```scala
type Result[A] = Either[NonEmptyList[String], A] // or
type Result[A] = Either[NonEmptyChain[String], A]
```

If you decide to use `NonEmptyList` or `NonEmptyChain,
you can optionally use the following type aliases provided by Cats:

```scala
import cats.data.EitherNel
import cats.data.EitherNec

type Result[A] = EitherNel[String, A] // or
type Result[A] = EitherNec[String, A]
```
