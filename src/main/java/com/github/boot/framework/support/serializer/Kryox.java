package com.github.boot.framework.support.serializer;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

import static de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer.registerSerializers;

/**
 * Created by cjh on 2017/3/28.
 */
@SuppressWarnings("restriction")
public class Kryox extends Kryo {

	private final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();

    private final ConcurrentHashMap<Class<?>, Constructor<?>> constructors = new ConcurrentHashMap<Class<?>, Constructor<?>>();

    public Kryox(){
        registerSerializers(this);
        //可以反序列化类属性变动的对象
        setDefaultSerializer(CompatibleFieldSerializer.class);
        ((Kryo.DefaultInstantiatorStrategy)getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        Constructor<?>[] constructors = type.getConstructors();
        for (Constructor<?> constructor : constructors){
            if ( constructor.getParameterTypes().length == 0 ) {
                return super.newInstance(type);
            }
        }
        return (T) newInstanceFromReflectionFactory(type);
    }

    private Object newInstanceFrom(Constructor<?> constructor) {
        try {
            return constructor.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstanceFromReflectionFactory(Class<T> type) {
        Constructor<?> constructor = constructors.get(type);
        if (constructor == null) {
            constructor = newConstructorForSerialization(type);
            Constructor<?> saved = constructors.putIfAbsent(type, constructor);
            if(saved!=null){
                constructor=saved;
            }
        }
        return (T) newInstanceFrom(constructor);
    }

    private <T> Constructor<?> newConstructorForSerialization(
            Class<T> type) {
        try {
            Constructor<?> constructor = REFLECTION_FACTORY
                    .newConstructorForSerialization(type,
                            Object.class.getDeclaredConstructor());
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}