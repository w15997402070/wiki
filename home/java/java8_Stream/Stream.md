# Java8流操作

[toc]



```java
static List<Dish> menuList = Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT), 
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER), 
            new Dish("pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("salmon", false, 450, Dish.Type.FISH));
```

## Filter

```java
@Test
public  void filter(){
    List<Dish> list = menuList.stream()
        .filter(m -> m.getCalories() > 300)
        .collect(Collectors.toList());
    list.forEach(m -> {
        System.out.println(m.getName());
    });
}
```



## java7和java8集合操作对比

```java (java7)
    List<Dish> lowCaloricDishes = new ArrayList<>();
    		for (Dish d : menu) {
    			if (d.getCalories() < 400) {
    				lowCaloricDishes.add(d);
    			}
    		}
    		Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
    			public int compare(Dish d1, Dish d2) {
    				return Integer.compare(d1.getCalories(), d2.getCalories());
    			}
    		});
    		List<String> lowCaloricDishesName = new ArrayList<>();
    		for (Dish d : lowCaloricDishes) {
    			lowCaloricDishesName.add(d.getName());
    		}
```

lowCaloricDishes它唯一的作用就是作为一次性的中间容器。在Java 8中，实现的细节被放在它本该归属的库里了。

```java (java8)
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import java.util.stream.Collectors;

    List<String> lowCaloricDishesName = menu.stream()
                                            .filter(d -> d.getCalories() < 400)
                                            .sorted(comparing(Dish::getCalories))
                                            .map(Dish::getName)
                                            .collect(Collectors.toList());

  //为了利用多核架构并行执行这段代码，你只需要把stream()换成parallelStream()：
```
