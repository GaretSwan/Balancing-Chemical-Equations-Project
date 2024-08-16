import java.util.Scanner;

// borrowed code - Author: Diane Kramer
public class Fraction {
  private int numerator, denominator;

  public Fraction() {
    numerator = denominator = 0;
  }

  public int getNumerator() {
    return numerator;
  }

  public void setNumerator(int num) {
    numerator = num;
  }

  public int getDenominator() {
    return denominator;
  }

  public void setDenominator(int den) {
    denominator = den;
  }

  public Fraction add(Fraction b) {
    // check preconditions
    if ((denominator == 0) || (b.denominator == 0))
      throw new IllegalArgumentException("invalid denominator");
    // find the lowest common denominator
    int common = lcd(denominator, b.denominator);
    // convert fractions to lcd
    Fraction commonA;
    Fraction commonB;
    commonA = convert(common);
    commonB = b.convert(common);
    // create new fraction to return as sum
    Fraction sum = new Fraction();
    // calculate sum
    sum.numerator = commonA.numerator + commonB.numerator;
    sum.denominator = common;
    // reduce the resulting fraction
    sum = sum.reduce();
    return sum;
  }

  public Fraction subtract(Fraction b) {
    // check preconditions
    if ((denominator == 0) || (b.denominator == 0))
      throw new IllegalArgumentException("invalid denominator");
    // find the lowest common denominator
    int common = lcd(denominator, b.denominator);
    // convert fractions to lcd
    Fraction commonA;
    Fraction commonB;
    commonA = convert(common);
    commonB = b.convert(common);
    // create new fraction to return as difference
    Fraction diff = new Fraction();
    // calculate difference
    diff.numerator = commonA.numerator - commonB.numerator;
    diff.denominator = common;
    // reduce the resulting fraction
    diff = diff.reduce();
    return diff;
  }

  public Fraction multiply(Fraction b) {
    // check preconditions
    if ((denominator == 0) || (b.denominator == 0))
      throw new IllegalArgumentException("invalid denominator");
    // create new fraction to return as product
    Fraction product = new Fraction();
    // calculate product
    product.numerator = numerator * b.numerator;
    product.denominator = denominator * b.denominator;
    // reduce the resulting fraction
    product = product.reduce();
    // cancel den negatives
    product.makeDenPos();
    return product;
  }

  public Fraction divide(Fraction b) {
    // check preconditions
    if ((denominator == 0) || (b.numerator == 0))
      throw new IllegalArgumentException("invalid denominator");
    // create new fraction to return as result
    Fraction result = new Fraction();
    // calculate result
    result.numerator = numerator * b.denominator;
    result.denominator = denominator * b.numerator;
    // reduce the resulting fraction
    result = result.reduce();
    // cancel den negatives
    result.makeDenPos();
    return result;
  }

  public void makeDenPos() {
    if (this.denominator < 0) {
      this.numerator *= -1;
      this.denominator *= -1;
    }
  }

  public double decimal() {
    // check preconditions
    if (denominator == 0)
      throw new IllegalArgumentException("invalid denominator");
    // create new double to return as result
    double result;
    // calculate result
    result = ((double) numerator / (double) denominator);

    return result;
  }

  // get zero
  public static Fraction zero() {
    Fraction zeroVal = new Fraction();

    zeroVal.setNumerator(0);
    zeroVal.setDenominator(1);

    return zeroVal;
  }

  // get negative one
  public static Fraction negativeOne() {
    Fraction negativeOneVal = new Fraction();

    negativeOneVal.setNumerator(-1);
    negativeOneVal.setDenominator(1);

    return negativeOneVal;
  }

  public void input(Scanner scnr) {
    // prompt user to enter numerator
    System.out.print("Please enter an integer for numerator: ");
    // get user input
    numerator = scnr.nextInt();
    // prompt user to enter denominator in a loop to prevent
    // an invalid (zero) value for denominator
    do {
      System.out.print("Please enter a non-zero integer for denominator: ");
      denominator = scnr.nextInt();
      // make sure it is non-zero
      if (denominator == 0)
        System.out.println("Invalid value.  Please try again.");
    } while (denominator == 0);
  }

  public void output() {
    System.out.print(this);
  }

  // get one
  public static Fraction one() {
    Fraction oneVal = new Fraction();

    oneVal.setNumerator(1);
    oneVal.setDenominator(1);

    return oneVal;
  }

  public String toString() {
    return numerator + "/" + denominator;
  }

  private int lcd(int denom1, int denom2) {
    int factor = denom1;
    while ((denom1 % denom2) != 0)
      denom1 += factor;
    return denom1;
  }

  private int gcd(int denom1, int denom2) {
    int factor = denom2;
    while (denom2 != 0) {
      factor = denom2;
      denom2 = denom1 % denom2;
      denom1 = factor;
    }
    return denom1;
  }

  private Fraction convert(int common) {
    Fraction result = new Fraction();
    int factor = common / denominator;
    result.numerator = numerator * factor;
    result.denominator = common;
    return result;
  }

  private Fraction reduce() {
    Fraction result = new Fraction();
    int common;
    // get absolute values for numerator and denominator
    int num = Math.abs(numerator);
    int den = Math.abs(denominator);
    // figure out which is less, numerator or denominator
    if (num > den)
      common = gcd(num, den);
    else if (num < den)
      common = gcd(den, num);
    else // if both are the same, don't need to call gcd
      common = num;

    // set result based on common factor derived from gcd
    result.numerator = numerator / common;
    result.denominator = denominator / common;
    return result;
  }
}