package org.makassi;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        /*
        5. Optional with Streams
            Optional and Stream integrate naturally. The most important bridge method is optional.stream() (Java
            9+), which converts an Optional into a Stream of 0 or 1 element.
        */

        //5.1 optional.stream() — the key bridge
        Optional<String> hello = Optional.of("hello");
        Stream<String> stream = hello.stream();

        Optional<String> empty2 = Optional.empty();
        Stream<String> stream2 = empty2.stream();

        //5.2 Flatten a Stream of Optionals
       List<Optional<String>> optionals  = List.of(
               Optional.of("Alice"),
               Optional.empty(),
               Optional.of("Bob"),
               Optional.empty(),
               Optional.of("Charlie")
        );

        List<String> names = optionals
                .stream()
                .flatMap(Optional::stream).toList(); // Java 9+ — cleanest approach with flatMap + Optional::stream

        System.out.println(names);
        List<String> names2 = optionals
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList(); // Java 8 — filter + map (more verbose)
        System.out.println(names2);

        // 5.3 Stream operations that return Optional
        //Many terminal Stream operations return Optional to handle the case where the stream is empty:
        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);
        // findFirst():Optional<T> - findAny():Optional<T>
        Optional<Integer> firstEven = numbers.stream().filter(n -> n % 2 == 0).findFirst();
        System.out.println(firstEven);
        Optional<Integer> anyEven = numbers.stream().filter(n -> n % 2 ==0 ).findAny();
        System.out.println(anyEven);

        Optional<Integer> max = numbers.stream().max(Integer::compareTo);
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        System.out.println(max); // Optional[9]
        System.out.println(min.get()); // 1

        // reduce() — Optional<T> (single-arg version)
        Optional<Integer> sum = numbers.stream().reduce(Integer::sum);
        System.out.println(sum); // Optional[31]
        // On an empty stream — all return Optional.empty()
        Optional<Integer> none = Stream.<Integer>empty().findFirst();
        System.out.println(none); // Optional.empty

        //5.4 Chaining Stream result with Optional methods
        // Find first name starting with 'A', uppercase it, or use default
        List<String> names3 = List.of("Alice", "Bob", "Charlie", "Aissata");
        String firstWithA = names3.stream().filter(n -> n.toLowerCase().contains("a"))
                .findFirst()
                .map(String::toUpperCase)
                .orElse("No such value");
        System.out.println(firstWithA);
        // Find longest name or throw
        String longest = names3.stream()
                .max(Comparator.comparingInt(String::length)) // Optional<String>
                .orElseThrow(() -> new RuntimeException("Empty list!"));
        System.out.println(longest); // Charlie

        //5.5 Collecting to Optional
        // Collectors.maxBy / minBy return Optional
        List<String> fruits = List.of("apple","banana","fig","kiwi");

        Optional<String> longest2 = fruits.stream()
                .max(Comparator.comparingInt(String::length));
        System.out.println(longest2); // Optional[banana]

        // reducing() collector also returns Optional
        Optional<String> concat = fruits.stream()
                .reduce((a, b) -> a + "," + b);
        System.out.println(concat); // Optional[apple,banana,fig,kiwi]

         //6. Antippatterns
        /* ■ Anti-pattern 1 — isPresent() + get()
            if (optional.isPresent()) {
                System.out.println(optional.get());
                Why it's bad Fix
                optional.get() without check Throws NoSuchElementException if empty Use orElseThrow() or orElse()
                as method parameter Forces callers to wrap values unnecessarily Use @Nullable or overloading instead
                as field type Not serializable, adds overhead Use null + @Nullable annotation
                isPresent() + get() patternVerbose, misses the point of Optional Use map/orElse/ifPresent instead
                null in Optional.of() NullPointerException immediately Use Optional.ofNullable()
                Returning Optional.of(null)Defeats the purpose entirely Return Optional.empty()
            }
            // ■ Fix
            optional.ifPresent(System.out::println);
        */
        /* Anti-pattern 2 — Optional as parameter
            void process(Optional<String> name) { ... }
        /* ■ Fix
            void process(String name) { ... } // let caller handle null
         */

        /* ■ Anti-pattern 3 — nested null check style
            Optional<String> opt = findName();
            String result = null;
            if (opt.isPresent()) {
                result = opt.get().toUpperCase();
            } else {
                result = "default";
            }
            // ■ Fix — one clean chain
            String result = findName().map(String::toUpperCase).orElse("default");
         */
    }
}