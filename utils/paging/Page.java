package com.example.guiex1.utils.paging;

import com.example.guiex1.domain.Entity;

public class Page<E>{
    private Iterable<E> elementsOnPage;
    private int totalNumberOfElements;

    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }
    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}
