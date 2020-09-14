# FactoryBean

创建Car类

```java
@Getter
@Setter
public class Car {

    private int maxSpeed;
    private String brand;
    private double price;
}
```

创建FactoryBean的实现类,通过在getObject()方法中返回Car的实例

```java
@Setter
@Getter
public class CarFactoryBean implements FactoryBean {
    private String carInfo;
    @Override
    public Object getObject() throws Exception {
        Car car = new Car();
        String [] infos = carInfo.split(",");
        car.setBrand(infos[0]);
        car.setMaxSpeed(Integer.parseInt(infos[1]));
        car.setPrice(Double.parseDouble(infos[2]));
        return car;
    }

    @Override
    public Class<?> getObjectType() {
        return Car.class;
    }
}
```

xml文件配置

```xml
	<bean id="car" class="com.spring.demo.bean.factoryBean.CarFactoryBean">
       <property name="carInfo" value="跑车,200,10000"/>
    </bean>
```

在通过`getBean('car')`,获取 `Car` 的实例时,`CarFactoryBean`的`getObject()`就可以返回`Car`的实例

测试类

```java
public class FactorBeanTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("application-content.xml");
        Car car = (Car)applicationContext.getBean("car");
        System.out.println(car.getBrand());
    }
}
```

