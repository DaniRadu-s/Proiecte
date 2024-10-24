
#include "buget.h"
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include "MyList.h"
#include "service.h"
#define _CRTDBG_MAP_ALLOC
#include <crtdbg.h>
int addBuget(MyList* l, char* type, int zi, float suma)
{
    Buget *p = createBuget(type, zi, suma);
    int succ = valideaza(p, l);
    if (succ != 0)
    {
        if(succ == 4) printf("Exista deja aceasta cheltuiala in ziua respectiva!\n");
        if(succ == 1) printf("Nu exista ziua respectiva!\n");
        if(succ == 2) printf("Nu exista tipul respectiv!\n");
        if(succ == 3) printf("Suma trebuie sa fie pozitiva!\n");
        return succ;
    }
    add(l, p);
    return 0;
}

void getAllBuget(MyList* l)
{
    int i;
    for(i=0;i<size(l);i++)
    {
        printf("%s %d %.2f\n", l->elems[i]->type, l->elems[i]->zi, l->elems[i]->suma);
    }
}

void test_getAllBuget()
{
    MyList *l = createEmpty();
    Buget *p = createBuget("altele", 2, 2);
    add(l, p);
    assert(size(l) == 1);
    assert(strcmp(l->elems[0]->type,"altele") == 0);
    assert(l->elems[0]->zi == 2);
    assert(l->elems[0]->suma == 2);
    destroy(l);
}

void test_all()
{
    testBuget();
    testValidare();
    test_destroy();
    test_resize();
    test_add();
    test_getAllBuget();
    test_modificare();
    testCreateEmpty();
    test_set();
    test_delete();
    test_sort_list();
    test_filtrare_type();
    test_filtrare_zi();
    test_filtrare_suma();
    testCopyList();
}

void start()
{
    MyList *Bugete = createEmpty();
    int ok = 1;
    while(ok)
    {
        printf("1. Add\n2. Modify\n3. Delete\n4. Afisare dupa o proprietate(tip,zi,suma)\n5. Afisare ordonate dupa o proprietate(suma,tip,zi)\n0. Iesire\n");
        int cmd = 0;
        scanf_s("%ld", &cmd);
        if(cmd == 1)
        {
            printf("Tip:");
            char type[30];
            scanf_s("%s", type, 30);
            printf("Zi:");
            int zi;
            scanf_s("%ld", &zi);
            printf("Suma:");
            float suma;
            scanf_s("%f", &suma);
            addBuget(Bugete, type, zi, suma);
        }
        else if(cmd == 2)
        {
            printf("Tip:");
            char tip[30];
            scanf_s("%s", tip, 30);
            printf("Zi:");
            int zi;
            scanf_s("%ld", &zi);
            printf("Suma:");
            float suma;
            scanf_s("%f", &suma);
            printf("Tipul pe care vrei sa-l pui:");
            char type[30];
            scanf_s("%s", type, 30);
            printf("Ziua pe care vrei sa o pui:");
            int day;
            scanf_s("%ld", &day);
            printf("Suma pe care vrei sa o pui:");
            float sum;
            scanf_s("%f", &sum);
            modificare(Bugete,tip,zi,suma,type,day,sum);
        }
        else if(cmd == 3)
        {
            printf("Tip:");
            char tip[30];
            scanf_s("%s", tip, 30);
            printf("Zi:");
            int zi;
            scanf_s("%ld", &zi);
            printf("Suma:");
            float suma;
            scanf_s("%f", &suma);
            Buget *p = createBuget(tip,zi,suma);
            delete(Bugete, p);
        }
        else if(cmd == 4)
        {
            char cuvant[30], criteriu[30];
            int numar;
            float sum;
            printf("Criteriu de filtrare:");
            scanf_s("%s",cuvant,30);
            MyList *rez = copyList(Bugete);
            if(strcmp(cuvant,"type") == 0)
            {
                printf("Alege tipul:");
                scanf_s("%s", criteriu, 30);
                filtrare_type(rez, criteriu);
            }
            if(strcmp(cuvant,"zi") == 0)
            {
                printf("Alege ziua:");
                scanf_s("%ld", &numar);
                filtrare_zi(rez, numar);
            }
            if(strcmp(cuvant,"suma") == 0)
            {
                printf("Alege suma:");
                scanf_s("%f", &sum);
                filtrare_suma(rez, sum);
            }
            getAllBuget(rez);
            printf("\n");
        }
        else if(cmd == 5)
        {
            char cuvant[30];
            int sortare;
            printf("Criteriu de sortare:");
            scanf_s("%s", cuvant, 30);
            MyList *rez = copyList(Bugete);
            if (strcmp(cuvant, "type") == 0) {
                printf("Alegere sortare: ");
                scanf_s("%ld", &sortare);
                sort_list(rez, cmptype, sortare);
            }
            if (strcmp(cuvant, "zi") == 0)
            {
                printf("Alegere sortare: ");
                scanf_s("%ld", &sortare);
                sort_list(rez, cmpzi, sortare);
            }
            if (strcmp(cuvant, "suma") == 0) {
                printf("Alegere sortare: ");
                scanf_s("%ld", &sortare);
                sort_list(rez, cmpsuma, sortare);
            }
            getAllBuget(rez);
            printf("\n");
        }
        else if(cmd == 0) break;
        else printf("Comanda invalida!");
    }
    destroy(Bugete);
}
int main(){
    test_all();
    start();
    _CrtDumpMemoryLeaks();
    return 0;
}