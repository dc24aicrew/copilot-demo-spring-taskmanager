package com.demo.copilot.taskmanager.test.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Custom Page implementation for testing that is guaranteed to be serializable
 */
public class TestPage<T> implements Page<T> {
    
    private final List<T> content;
    private final long totalElements;
    private final int number;
    private final int size;
    private final int totalPages;
    
    public TestPage(List<T> content) {
        this(content, 0, content.size(), content.size());
    }
    
    public TestPage(List<T> content, int number, int size, long totalElements) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
    
    @Override
    public int getTotalPages() {
        return totalPages;
    }
    
    @Override
    public long getTotalElements() {
        return totalElements;
    }
    
    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        List<U> converted = content.stream().map(converter).collect(java.util.stream.Collectors.toList());
        return new TestPage<>(converted, number, size, totalElements);
    }
    
    @Override
    public int getNumber() {
        return number;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public int getNumberOfElements() {
        return content.size();
    }
    
    @Override
    public List<T> getContent() {
        return content;
    }
    
    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }
    
    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }
    
    @Override
    public boolean isFirst() {
        return number == 0;
    }
    
    @Override
    public boolean isLast() {
        return number == totalPages - 1;
    }
    
    @Override
    public boolean hasNext() {
        return number < totalPages - 1;
    }
    
    @Override
    public boolean hasPrevious() {
        return number > 0;
    }
    
    @Override
    public Pageable nextPageable() {
        return Pageable.ofSize(size).withPage(number + 1);
    }
    
    @Override
    public Pageable previousPageable() {
        return Pageable.ofSize(size).withPage(number - 1);
    }
    
    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}