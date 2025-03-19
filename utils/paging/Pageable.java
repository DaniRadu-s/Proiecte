package com.example.guiex1.utils.paging;

import com.example.guiex1.domain.Entity;

public class Pageable{
    private int pageSize;
    private int pageNumber;
    public Pageable(int pageSize, int pageNumber){
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public int getPageSize(){
        return pageSize;
    }
    public int getPageNumber(){
        return pageNumber;
    }
}
