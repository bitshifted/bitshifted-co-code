package co.bitshifted.sample;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        var greeter = new Greeter();
        String name = null;
        if(args.length > 0) {
            name = args[0];
        }
        greeter.greet(name);
    }
}
