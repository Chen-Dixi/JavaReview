package dixi.bupt.lazyLoad;



class TestClass {
    public static void main(String[] args) {
        // 并没有加载 SubClass
        System.out.println(SubClass.value);
    }

    public static class SuperClass {

        static {
            System.out.println("SuperClass init");
        }
        public static int value = 10;
    }

    static class  SubClass extends SuperClass {
        static {
            System.out.println("SubClass init");
        }
    }
}

