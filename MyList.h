#pragma once
#include "buget.h"
typedef Buget* ElemType;
typedef struct {
    ElemType* elems;
    int lg;
    int cp;
} MyList;

typedef int(*comparare)(Buget*, Buget*);

MyList *createEmpty();

void testCreateEmpty();

void destroy(MyList* l);

void test_destroy();

void test_resize();

int addBuget(MyList* l,char* type, int zi, float suma);

void getAllBuget(MyList *l);

void test_getAllBuget();


ElemType set(MyList* l,int poz, char* type, int day, float sum);

int size(MyList* l);

void sort_list(MyList* l, comparare cmptype,  int sortare);

void filtrare_type(MyList* l, char criteriu[30]);

void filtrare_zi(MyList* l, int numar);

void filtrare_suma(MyList* l, float sum);

void add(MyList* l, ElemType el);

void test_add();

void delete(MyList* l, ElemType el);

void resize(MyList *l);

MyList *copyList(MyList* l);

void testCopyList();

void test_sort_list();

void test_set();

void test_delete();

void test_filtrare_type();

void test_filtrare_zi();

void test_filtrare_suma();

int cmptype(Buget* a, Buget* b);

int cmpzi(Buget* a, Buget* b);

int cmpsuma(Buget* a, Buget* b);