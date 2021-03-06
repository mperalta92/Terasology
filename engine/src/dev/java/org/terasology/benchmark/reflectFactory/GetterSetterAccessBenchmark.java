/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.benchmark.reflectFactory;

import org.terasology.benchmark.AbstractBenchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.reflection.reflect.FieldAccessor;
import org.terasology.reflection.reflect.InaccessibleFieldException;
import org.terasology.reflection.reflect.ReflectFactory;

/**
 * @author Immortius
 */
public class GetterSetterAccessBenchmark extends AbstractBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(ConstructionBenchmark.class);
    private ReflectFactory reflectFactory;
    private FieldAccessor accessor;
    private int i;
    private GetterSetterComponent comp;

    public GetterSetterAccessBenchmark(ReflectFactory reflectFactory) {
        super("Getter/Setter access via " + reflectFactory.getClass().getSimpleName(), 100000000, new int[]{100000000, 100000000});
        this.reflectFactory = reflectFactory;
    }

    @Override
    public void setup() {
        i = 0;
        comp = new GetterSetterComponent();
        try {
            accessor = reflectFactory.createFieldAccessor(GetterSetterComponent.class, GetterSetterComponent.class.getDeclaredField("value"));
        } catch (InaccessibleFieldException | NoSuchFieldException e) {
            logger.error("Failed to establish field accessor object", e);
        }
    }

    @Override
    public void run() {
        accessor.setValue(comp, i++);
        int val = (int) accessor.getValue(comp);
        val++;
    }
}
