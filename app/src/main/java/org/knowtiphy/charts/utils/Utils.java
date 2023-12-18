package org.knowtiphy.charts.utils;

public class Utils
{
  //  TODO -- should really move this into the callers space
  private static final String[] FORMATS = new String[10];

  public static String formatDecimal(Number value, int numDigits)
  {
    if(numDigits >= FORMATS.length)
    {
      var formatString = "%%.%df".formatted(numDigits);
      return java.lang.String.format(formatString, value);
    }

    if(FORMATS[numDigits] == null)
    {
      FORMATS[numDigits] = "%%.%df".formatted(numDigits);
    }

    return java.lang.String.format(FORMATS[numDigits], value);
  }
}