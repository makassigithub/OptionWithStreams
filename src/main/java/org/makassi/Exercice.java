package org.makassi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Exercice {
    public static void main(String[] args) {
        System.out.println(exercise1("1"));
        System.out.println(exercise2("2"));
        System.out.println(exercise2("6"));
        System.out.println(exercise3());
        System.out.println(exercise4());
        System.out.println(exercise5());
        System.out.println(exercise6());
    }

    static Optional<String> exercise1(String id){
        //Given this map, write a method that returns an Optional for a key.
        Map<String,String> users = Map.of("1","Alice","2","Bob","3","Charlie");
       return Optional.ofNullable(users.get(id));
    }

    static String exercise2(String id){
        //Using findUser() from ex.1, find user "2", uppercase the name, or return "Unknown".
       return exercise1(id).map(String::toUpperCase).orElse("Unknown");
    }

    static List<Integer> exercise3(){
        //Given a List>, collect only the present even numbers.
        List<Optional<Integer>> opts =List.of(Optional.of(1), Optional.empty(), Optional.of(4), Optional.of(7), Optional.of(2));

        Predicate<Optional<Integer>> isEven = x -> x.isPresent() && x.get()%2==0;

        List<Integer> fromJava9 =  opts.stream()
                .filter(isEven)
               .flatMap(Optional::stream)
               .toList();

        List<Integer> beforeJava9 = opts.stream()
                .filter(Optional::isPresent)
                .filter(isEven)
                .map(Optional::get)
                .toList();

        return fromJava9;
    }

    static String exercise4(){
        //Model a User with Optional and Address with Optional city. Write a method getCity(User) that safely
        //extracts the city string.
        class Address {
            Optional<String> city;
            Optional<String> getCity() { return city; }
        }
        class User {
            Optional<Address> address;
            Optional<Address> getAddress() { return address; }
        }

        Function<User,String> getCity = user -> {
            return user.getAddress()
                    .flatMap(Address::getCity)
                    .orElse("Unknown location");
        };

        Address address = new Address();
             address.city = Optional.of("Montreal");
        User user = new User();
             user.address = Optional.of(address);
        return getCity.apply(user);
    }

    static String exercise5(){
        //Given List.of("Salif","Brahima","Maimouna","Aissata"), find the first name longer than 6 chars, trim it,
        //uppercase it, or return "NONE".

        List<String> names = List.of("Salif","Brahima","Maimouna","Aissata");

        return names.stream()
                .filter(n->n.length()>6)
                .findFirst()
                .map(String::trim)
                .map(String::toUpperCase)
                .orElse("NONE");
    }

    static String exercise6(){
        //Simulate a config system: try getFromEnv(), then getFromFile(), then default "localhost". Each method
        //returns Optional. Chain them with or().

        class Provider {
            static String computeDefault(String defaultValue) {
                return defaultValue;
            }

            static Optional<String> getFromEnv() {
                return Optional.of("from_env");
            }

            static Optional<String> getFromFile() {
                return Optional.of("form_file");
            }
        }

        return Provider.getFromEnv()
                .or(Provider::getFromFile)
                .or(()->Optional.of(Provider.computeDefault("localhost")))
                .orElseThrow();
    }

    static List<Integer> exercise7(){
        //Write a method Optional parsePositive(String s) that: parses s as Integer (return empty on exception),
        //filters out non-positive values, and returns the result.

        Function<String,Optional<Integer>> parsePositive = s -> {
            try {
                return Optional.of(Integer.parseInt(s))
                        .filter(n -> n > 0);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        };

        List<String> inputs = List.of("3","abc","-1","7","0","12");

        return inputs.stream()
                .map(parsePositive)
                .flatMap(Optional::stream)
                .toList();
    }
}
