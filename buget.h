#pragma once
typedef struct {
    char type[30];
    int zi;
    float suma;
} Buget;

Buget *createBuget(char* type, int zi, float suma);

void testBuget();

void destroyBuget(Buget* p);
