package ru.alex.professionalslanguageapi.mapper;

public abstract class ReadMapper<E, D> extends Mapper {
    public abstract D toDto(E entity);
}
