package com.jason.liu.env.adapter;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-06 10:40:19
 * @todo
 */
public class AnnotationTypeValueFilter<A extends Annotation> extends AnnotationTypeFilter {

    private final Class<A> annotationType;

    public AnnotationTypeValueFilter(Class<A> annotationType) {
        super(annotationType, false, false);
        this.annotationType = annotationType;
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        if (annotationMetadata.hasAnnotation(annotationType.getName())) {
            try {
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                Class<?> clazz = ClassUtils.forName(classMetadata.getClassName(), getClass().getClassLoader());
                A annotation = clazz.getAnnotation(annotationType);
                if (null == annotation) {
                    return false;
                }
                return this.matchAnnotationMetadata(annotation);
            } catch (Throwable ex) {
                // Class not regularly loadable - can't determine a match that way.
            }
        }
        return false;
    }

    protected boolean matchAnnotationMetadata(A annotation) {
        return true;
    }

}
