**简单的反射链式调用帮助类**

```java

   LinkedReflect.Value<Owner> value = LinkedReflect.Value.obtain();//use for peek
    //getCar().getOwner().mHouse.mAddress
   Object mAddress = LinkedReflect.with(obj)
                //use obj.getClass() 
                // or set explicitly with   .clazz(SuperCls.class)
                .method("getCar") 
                .method("getOwner",false) 
                .peek(value)//use to peek owner when you care it
                .field("mHouse")
                .field("mAddress")
                .get(); //use to get final return value, mAddress

   Owner owner=value.obj;

   //mPerson.rentCar(int,Driver).driving(Integer,Driver).mSpeed
    LinkedReflect.with(obj)
					.parnt()//父类的私有字段
                    .field("mPerson")
                    .method("rentCar", 80, mDriver) //getCar(int,Driver)
                    //参数存在基本类型的包装类型时 应使用下面的方法 flying(Integer,Driver)
                    .method("driving", new Object[]{800,mDriver},Integer.class,Driver.class) 
                    .field("mSpeed")
                    .clear(); //清理static field
```