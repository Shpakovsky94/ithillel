package lesson19.designPatterns.builder.lombokVersion;

public class Main {

    //https://www.baeldung.com/lombok-builder
    public static void main(String[] args) {
        NutritionFacts nutritionFacts = NutritionFacts.builder()
                .fat(10)
                .calories(110)
                .sodium(10)
                .build();

        NutritionFacts nutritionFacts2 = new NutritionFacts();
        nutritionFacts2.setCalories(220);


        System.out.println(nutritionFacts.toString());
        System.out.println(nutritionFacts2.toString());


    }
}
