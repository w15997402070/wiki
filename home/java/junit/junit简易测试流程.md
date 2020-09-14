
### junit简单测试流程

从简单的测试开始

```java
    public class SimpleTest extends TestCase {

        protected int fValue1;
        protected int fValue2;

        protected void setUp() {
            fValue1= 2;
            fValue2= 3;
        }

        public static Test suite() {
           return new TestSuite(SimpleTest.class);
        }

        public static void main (String[] args) {
            junit.textui.TestRunner.run(suite());
        }

        public void testAdd() {
            double result= fValue1 + fValue2;
        // forced failure result == 5
            assertTrue(result == 6);
        }
    }
```

1. 首先从main方法开始

- 从`java.textui.TestRunner`的run方法启动测试
- 

