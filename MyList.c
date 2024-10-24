#include "MyList.h"
#include "service.h"
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>

MyList *createEmpty() {
    MyList *rez = malloc(sizeof(MyList));
    rez->cp = 50;
    rez->elems = malloc(sizeof(ElemType)*rez->cp);
    rez->lg = 0;
    return rez;
}

void testCreateEmpty()
{
    MyList *l = createEmpty();
    assert(size(l) == 0);
    destroy(l);
}

void destroy(MyList* l) {
    int i;
    for(i=0;i<l->lg;i++)
    {
        ElemType el = l->elems[i];
        destroyBuget(el);
    }
    l->lg = 0;
    free(l->elems);
    free(l);
}

void test_destroy()
{
    MyList *l = createEmpty();
    add(l, createBuget("altele",2,2));
    destroy(l);
}

void resize(MyList* l)
{
    l->cp *= 2;
    ElemType *aux = (ElemType *)realloc(l->elems, sizeof(ElemType) * l->cp);
    l->elems = aux;
}

void test_resize()
{
    MyList *l = createEmpty();
    resize(l);
    add(l, createBuget("altele", 1, 2));
    add(l, createBuget("altele", 2, 2));
    add(l, createBuget("altele", 3, 2));
    add(l, createBuget("altele", 4, 2));
    add(l, createBuget("altele", 5, 2));
    add(l, createBuget("altele", 6, 2));
    add(l, createBuget("altele", 7, 2));
    add(l, createBuget("altele", 8, 2));
    add(l, createBuget("altele", 9, 2));
    add(l, createBuget("altele", 10, 2));
    add(l, createBuget("altele", 11, 2));
    add(l, createBuget("altele", 12, 2));
    add(l, createBuget("altele", 13, 2));
    add(l, createBuget("altele", 14, 2));
    add(l, createBuget("altele", 15, 2));
    add(l, createBuget("altele", 16, 2));
    add(l, createBuget("altele", 17, 2));
    add(l, createBuget("altele", 18, 2));
    add(l, createBuget("altele", 19, 2));
    add(l, createBuget("altele", 20, 2));
    add(l, createBuget("altele", 21, 2));
    add(l, createBuget("altele", 22, 2));
    add(l, createBuget("altele", 23, 2));
    add(l, createBuget("altele", 24, 2));
    add(l, createBuget("altele", 25, 2));
    add(l, createBuget("altele", 26, 2));
    add(l, createBuget("altele", 27, 2));
    add(l, createBuget("altele", 28, 2));
    add(l, createBuget("altele", 29, 2));
    add(l, createBuget("altele", 30, 2));
    add(l, createBuget("altele", 31, 2));
    add(l, createBuget("imbracaminte", 1, 2));
    add(l, createBuget("imbracaminte", 2, 2));
    add(l, createBuget("imbracaminte", 3, 2));
    add(l, createBuget("imbracaminte", 4, 2));
    add(l, createBuget("imbracaminte", 5, 2));
    add(l, createBuget("imbracaminte", 6, 2));
    add(l, createBuget("imbracaminte", 7, 2));
    add(l, createBuget("imbracaminte", 8, 2));
    add(l, createBuget("imbracaminte", 9, 2));
    add(l, createBuget("imbracaminte", 10, 2));
    add(l, createBuget("imbracaminte", 11, 2));
    add(l, createBuget("imbracaminte", 12, 2));
    add(l, createBuget("imbracaminte", 13, 2));
    add(l, createBuget("imbracaminte", 14, 2));
    add(l, createBuget("imbracaminte", 15, 2));
    add(l, createBuget("imbracaminte", 16, 2));
    add(l, createBuget("imbracaminte", 17, 2));
    add(l, createBuget("imbracaminte", 18, 2));
    add(l, createBuget("imbracaminte", 19, 2));
    add(l, createBuget("imbracaminte", 20, 2));
    destroy(l);
}


ElemType set(MyList* l,int poz, char type[30], int day, float sum)
{
    strcpy_s(l->elems[poz]->type, 30, type);
    l->elems[poz]->zi = day;
    l->elems[poz]->suma = sum;
    return l->elems[poz];
}

void test_set()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("transport", 17, 3);
    add(l, p);
    set(l, 0, "telefon&internet", 7, 8);
    assert(l->elems[0]->zi == 7);
    assert((int)l->elems[0]->suma == 8);
    assert(strcmp(l->elems[0]->type,"telefon&internet") == 0);
    destroy(l);
}
int size(MyList* l) {
    return l->lg;
}
void add(MyList* l, ElemType el) {
    if (l->lg == l->cp)
        resize(l);
    l->elems[l->lg++] = el;
}

void test_add()
{
    MyList *l = createEmpty();
    addBuget(l,"altele",1,2);
    destroy(l);
}

MyList* copyList(MyList* l) {
    MyList* rez = createEmpty();
    for (int i = 0; i < size(l); i++) {
        Buget* copyx = createBuget(l->elems[i]->type, l->elems[i]->zi, l->elems[i]->suma);
        add(rez, copyx);
    }
    return rez;
}

void testCopyList() {
    MyList* l = createEmpty();
    Buget* p = createBuget("a", 10, 10);
    add(l, p);
    Buget* t = createBuget("a2", 12, 20);
    add(l, t);
    MyList* l2 = copyList(l);
    assert(size(l2) == 2);
    Buget* s = l2->elems[0];
    assert(strcmp(s->type, "a") == 0);
    destroy(l2);
    destroy(l);
}

void delete(MyList* l, ElemType el)
{
    for(int i = 0;i < size(l);i++)
    {
        if(strcmp(el->type,l->elems[i]->type) == 0 && el->zi == l->elems[i]->zi && el->suma == l->elems[i]->suma) {
            for (int j = i + 1; j < size(l); j++) {
                l->elems[j - 1] = l->elems[j];
            }
            i--;
            l->lg--;
        }
    }
}

void test_delete()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("altele", 29, 3);
    add(l, p);
    delete(l, p);
    destroyBuget(p);
    assert(size(l) == 0);
    destroy(l);
}

int cmptype(Buget* a, Buget* b) {
    return  strcmp(a->type,b->type);
}

int cmpzi(Buget* a, Buget* b) {
    return a->zi - b->zi;
}

int cmpsuma(Buget* a, Buget* b) {
    return (int)(a->suma - b->suma);
}
void sort_list(MyList* l, comparare cmptype, int sortare)
{
    Buget *aux;
    int i, j;
    if(sortare == 1)
        for(i=0;i<size(l)-1;i++) {
            for (j = i + 1; j < size(l); j++) {
                if (cmptype(l->elems[i], l->elems[j])>=0) {
                    aux = l->elems[i];
                    l->elems[i] = l->elems[j];
                    l->elems[j] = aux;
                }
            }
        }
    if(sortare == -1)
        for (i = 0;i < size(l) - 1;i++) {
            for (j = i + 1; j < size(l); j++) {
                if (cmptype(l->elems[i], l->elems[j]) < 0) {
                    aux = l->elems[i];
                    l->elems[i] = l->elems[j];
                    l->elems[j] = aux;
                }
            }
        }
}

void test_sort_list()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("telefon&internet", 2, 3);
    add(l, p);
    Buget *t = createBuget("altele", 4, 5);
    add(l, t);
    sort_list(l, cmptype, 1);
    assert(l->elems[0]->zi == 4);
    assert((int)l->elems[0]->suma == 5);
    assert(strcmp(l->elems[0]->type,"altele") == 0);
    assert(l->elems[1]->zi == 2);
    assert((int)l->elems[1]->suma == 3);
    assert(strcmp(l->elems[1]->type,"telefon&internet") == 0);
    sort_list(l, cmptype, -1);
    assert(l->elems[1]->zi == 4);
    assert((int)l->elems[1]->suma == 5);
    assert(strcmp(l->elems[1]->type, "altele") == 0);
    assert(l->elems[0]->zi == 2);
    assert((int)l->elems[0]->suma == 3);
    assert(strcmp(l->elems[0]->type, "telefon&internet") == 0);
    destroy(l);
    MyList *l2 = createEmpty();
    Buget *p2 = createBuget("telefon&internet", 4, 3);
    add(l2, p2);
    Buget *t2 = createBuget("altele", 2, 5);
    add(l2, t2);
    sort_list(l2, cmpzi, 1);
    assert(l2->elems[0]->zi == 2);
    assert((int)l2->elems[0]->suma == 5);
    assert(strcmp(l2->elems[0]->type,"altele") == 0);
    assert(l2->elems[1]->zi == 4);
    assert((int)l2->elems[1]->suma == 3);
    assert(strcmp(l2->elems[1]->type,"telefon&internet") == 0);
    sort_list(l2,cmpzi,  -1);
    assert(l2->elems[1]->zi == 2);
    assert((int)l2->elems[1]->suma == 5);
    assert(strcmp(l2->elems[1]->type, "altele") == 0);
    assert(l2->elems[0]->zi == 4);
    assert((int)l2->elems[0]->suma == 3);
    assert(strcmp(l2->elems[0]->type, "telefon&internet") == 0);
    destroy(l2);
    MyList *l3 = createEmpty();
    Buget *p3 = createBuget("telefon&internet", 4, 7);
    add(l3, p3);
    Buget *t3 = createBuget("altele", 2, 5);
    add(l3, t3);
    sort_list(l3,cmpsuma,  1);
    assert(l3->elems[0]->zi == 2);
    assert((int)l3->elems[0]->suma == 5);
    assert(strcmp(l3->elems[0]->type,"altele") == 0);
    assert(l3->elems[1]->zi == 4);
    assert((int)l3->elems[1]->suma == 7);
    assert(strcmp(l3->elems[1]->type,"telefon&internet") == 0);
    sort_list(l3,cmpsuma,  -1);
    assert(l3->elems[1]->zi == 2);
    assert((int)l3->elems[1]->suma == 5);
    assert(strcmp(l3->elems[1]->type, "altele") == 0);
    assert(l3->elems[0]->zi == 4);
    assert((int)l3->elems[0]->suma == 7);
    assert(strcmp(l3->elems[0]->type, "telefon&internet") == 0);
    destroy(l3);
}

void filtrare_type(MyList *l, char criteriu[30])
{
    int i;
    for(i=0;i<size(l);i++)
        if(strcmp(l->elems[i]->type,criteriu) != 0) {
            delete(l, l->elems[i]);
            i--;
        }
}

void test_filtrare_type()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("telefon&internet", 4, 3);
    add(l, p);
    Buget *t = createBuget("altele", 2, 5);
    add(l, t);
    filtrare_type(l, "altele");
    destroyBuget(p);
    assert(l->elems[0]->zi == 2);
    assert((int)l->elems[0]->suma == 5);
    assert(strcmp(l->elems[0]->type,"altele") == 0);
    assert(size(l) == 1);
    destroy(l);
}

void filtrare_zi(MyList *l, int numar)
{
    int i;
    for(i=0;i<size(l);i++)
        if(l->elems[i]->zi != numar) {
            delete(l, l->elems[i]);
            i--;
        }
}

void test_filtrare_zi()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("telefon&internet", 4, 3);
    add(l, p);
    Buget *t = createBuget("altele", 2, 5);
    add(l, t);
    filtrare_zi(l, 2);
    destroyBuget(p);
    assert(l->elems[0]->zi == 2);
    assert((int)l->elems[0]->suma == 5);
    assert(strcmp(l->elems[0]->type,"altele") == 0);
    assert(size(l) == 1);
    destroy(l);
}

void filtrare_suma(MyList *l, float sum)
{
    int i;
    for(i=0;i<size(l);i++)
        if(l->elems[i]->suma != sum) {
            delete(l, l->elems[i]);
            i--;
        }
}

void test_filtrare_suma()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("telefon&internet", 4, 3);
    add(l, p);
    Buget *t = createBuget("altele", 2, 5);
    add(l, t);
    filtrare_suma(l, 5);
    destroyBuget(p);
    assert(l->elems[0]->zi == 2);
    assert((int)l->elems[0]->suma == 5);
    assert(strcmp(l->elems[0]->type,"altele") == 0);
    assert(size(l) == 1);
    destroy(l);
}

