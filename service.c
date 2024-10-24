#include "MyList.h"
#include "service.h"
#include <string.h>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>

int valideaza(Buget *p, MyList* l) {
    if (p->zi<1 || p->zi>31) {
        return 1;
    }
    if (strcmp("telefon&internet",p->type) != 0 &&
        strcmp("imbracaminte",p->type) != 0 &&
        strcmp("transport",p->type) != 0 &&
        strcmp("mancare",p->type) != 0 &&
        strcmp("altele",p->type) != 0) {
        return 2;
    }
    if (p->suma < 0) {
        return 3;
    }
    int i;
    for(i=0;i<size(l);i++) {
        if(strcmp(l->elems[i]->type, p->type) == 0 && l->elems[i]->zi == p->zi) {
            return 4;
        }
    }
    return 0;
}

void testValidare()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("tableta", 12, 100);
    assert(valideaza(p, l) == 2);
    destroyBuget(p);
    Buget *t = createBuget("telefon&internet", 32, 100);
    assert(valideaza(t,l)==1);
    destroyBuget(t);
    Buget *x = createBuget("telefon&internet", 12, -3);
    assert(valideaza(x,l)==3);
    destroyBuget(x);
    Buget *s = createBuget("imbracaminte", 12, 10);
    assert(valideaza(s, l) == 0);
    add(l, s);
    Buget* y = createBuget("imbracaminte", 12, 10);
    assert(valideaza(y, l) == 4);
    destroyBuget(y);
    destroy(l);
}

void modificare(MyList *l, char* tip, int zi, float suma, char* type, int day, float sum)
{
    int i, ok = 0;
    for(i=0;i<size(l);i++)
    {   
        if(strcmp(tip, l->elems[i]->type) == 0 && zi == l->elems[i]->zi && suma == l->elems[i]->suma)
        {
            Buget *t = createBuget(type, day, sum);
            int ver = valideaza(t, l);
            if(ver == 0) {
                l->elems[i] = set(l, i, type, day, sum);
                ok = 1;
            } 
            destroyBuget(t);
        }  
    }
    if (ok) return;
}

void test_modificare()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("altele", 23, 2);
    add(l, p);
    modificare(l, "altele", 23, 2,"imbracaminte", 20, 8);
    assert(l->elems[0]->zi == 20);
    assert(l->elems[0]->suma == 8);
    assert(strcmp(l->elems[0]->type,"imbracaminte")==0);
    destroy(l);
}

