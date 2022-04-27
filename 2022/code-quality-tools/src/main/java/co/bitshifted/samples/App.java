package co.bitshifted.samples;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        var cl = new InvalidClass();
        cl.Invalid_method(5);

        System.out.println( "Hello World!" );
    }
}
