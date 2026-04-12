package org.makassi;

import java.util.Optional;
import java.util.function.Supplier;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
     /*
        1. Concept:
        Warning: Optional is NOT a replacement for every null check. Use it as a return type for methods
        that may not return a value. Never use it as a field type or method parameter.
     */

        Supplier<String> getUserName = () -> "userName";
        Optional
                .ofNullable(getUserName.get())
                .map(String::toUpperCase)
                .ifPresent(System.out::println);

        /*
           2 Creation
           Method Description Throws if null?
            Optional.of(value) Wraps a non-null value /Yes — NullPointerException
            Optional.ofNullable(value) Wraps a value that may be null/ No — returns empty()
            Optional.empty() Creates an empty Optional / N/A
        */
        // Optional.of() — value must NOT be null
        Optional<String> opt1 = Optional.of("hello");
        //Optional<String> opt2 = Optional.of(null); // NullPointerException!
        // Optional.ofNullable() — value CAN be null
        Optional<String> opt3 = Optional.ofNullable("hello"); // Optional[hello]
        Optional<String> opt4 = Optional.ofNullable(null); // Optional.empty
        // Optional.empty() — explicitly empty
        Optional<String> opt5 = Optional.empty(); // Optional.empty

        /*
        3: Checking & Retrieving Values
            isPresent(): boolean (true if value exists)
            isEmpty(): boolean (true if empty (Java 11+))
            get() :T (Returns value or throws NoSuchElementException)
            orElse(other): T (Returns value or the given default)
            orElseGet(supplier) : T (Returns value or calls supplier for default)
            orElseThrow() : T (Returns value or throws NoSuchElementException (Java 10+))
            orElseThrow(exSupplier) :T (Returns value or throws custom exception)

            NB:
                orElse vs orElseGet — an important difference
                orElse() always evaluates its argument even if the Optional is present. orElseGet() is lazy — it only calls
                the supplier if the Optional is empty.
            NB:
                orElseThrow — preferred over get()
                "get" throws NoSuchElementException with no context while orElseThrow can throws with a meaningful message
        * */

        Optional<String> opt = Optional.of("hello");
        class Provider {
            static String computeDefault(String defaultValue) {
                System.out.println(defaultValue);
                return "";
            }

            static Optional<String> getFromEnv() {
                return Optional.of("from_env");
            }

            static Optional<String> getFromFile() {
                return Optional.of("form_file");
            }
        }
        // orElse — computeDefault() is ALWAYS called (even though opt is present)
        String r1 = opt.orElse(Provider.computeDefault("orElse"));
        // orElseGet — computeDefault() is ONLY called if opt is empty
        String r2 = opt.orElseGet(() -> Provider.computeDefault("orElseGet"));
        // Rule: prefer orElseGet() when the default is expensive to compute

        getUserName = ()-> null;
        try {
            String value = Optional.ofNullable(getUserName.get()).orElseThrow(
                    () -> new RuntimeException("getUserName returned null")
            );
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        /*
           4. Transforming Optional Value
        */

        Optional<String> name = Optional.of("brahima");
        Optional<String> upper = name.map(String::toUpperCase);
        System.out.println(upper); // Optional[BRAHIMA]

        // Chain multiple maps
        Optional<Integer> len = name
                .map(String::toUpperCase)
                .map(String::length);
        System.out.println(len); // Optional[7]

        // map on empty Optional — safely does nothing
        Optional<String> empty = Optional.empty();
        Optional<String> result = empty.map(String::toUpperCase);
        System.out.println(result); // Optional.empty

        //flatMap() — avoid nested Optionals
        // Without flatMap — you get Optional<Optional<String>>
                Optional<String> name_2 = Optional.of("brahima");
                Optional<Optional<String>> nested = name.map(s -> Optional.of(s.toUpperCase()));
                // Optional[Optional[BRAHIMA]] — awkward!
                // With flatMap — stays flat
                Optional<String> flat = name_2.flatMap(s -> Optional.of(s.toUpperCase()));
                System.out.println(flat); // Optional[BRAHIMA]
                // Real world example — chaining methods that return Optional
                // Optional<String> findUser(int id) { ... }
                // Optional<String> getEmail(String user) { ... }
                // Optional<String> email = findUser(42)
                // .flatMap(user -> getEmail(user)); // no nested Optional!

             Optional.of( 4)
                     .map(Math::sqrt)
                     .map(x -> Optional.of(x * x))
                     .flatMap(x -> x)
                     .map(x->x*5)
                     .ifPresent(System.out::println);

        //ifPresentOrElse() — Java 9+
        Optional<String> name_3 = Optional.ofNullable(getUserName.get());
        name.ifPresentOrElse(
                n -> System.out.println("Hello, " + n), // present
                () -> System.out.println("No name found") // empty
        );

       // or() — fallback Optional — Java 9+
        Optional<String> primary = Optional.empty();
        Optional<String> secondary = Optional.of("fallback");
        Optional<String> result2 = primary.or(() -> secondary);
        System.out.println(result2); // Optional[fallback]

        // Useful for chaining multiple optional sources
        Optional<String> config = Provider.getFromEnv()
                .or(Provider::getFromFile)
                .or(() -> Optional.of("default"));
    }
}