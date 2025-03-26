package ru.alex.professionalslanguageapi.mapper;

import java.util.function.Consumer;

public abstract class EditMapper<E, D> extends Mapper {
    public abstract E updateFromDto(E entity, D dto);

    protected <T> void updateIfNotNull(Consumer<T> setter, T value) {
        if(value != null) {
            setter.accept(value);
        }
    }
}
