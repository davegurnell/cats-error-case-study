# Cats Error Handling Workshop

## Total and Average

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

def total(
  layerId: String,
  propId: String,
  sw: Option[String],
  ne: Option[String]
): Either[String, String] =
  ???

def average(
  layerId: String,
  propId: String,
  sw: Option[String],
  ne: Option[String]
): Either[String, String] =
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
