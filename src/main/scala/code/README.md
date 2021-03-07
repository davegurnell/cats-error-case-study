# Cats Error Handling Workshop

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
