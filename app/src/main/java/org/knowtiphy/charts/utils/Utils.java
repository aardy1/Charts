package org.knowtiphy.charts.utils;

public class Utils
{
  public static String formatDecimal(Number value, int numDigits)
  {
    var formatString = "%%.%df".formatted(numDigits);
    return String.format(formatString, value);
  }
}