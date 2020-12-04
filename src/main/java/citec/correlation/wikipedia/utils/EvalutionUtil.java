package citec.correlation.wikipedia.utils;

public class EvalutionUtil {

  public static void ifFalseCrash(boolean value, String message) {
    if (! value) {
      System.out.println(message);
      System.exit(-1);
    }
  }
}
