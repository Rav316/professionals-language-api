package ru.alex.professionalslanguageapi.mapper;

public abstract class CreateMapper<E, D> extends Mapper {
    public abstract E toEntity(D dto);
}
