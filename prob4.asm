bits 32 ; assembling for the 32 bits architecture

; declare the EntryPoint (a label defining the very first instruction of the program)
global start        
global _numere
; declare external functions needed by our program
extern exit,printf,scanf             
import exit msvcrt.dll    
import printf msvcrt.dll     
import scanf msvcrt.dll      
; our data is declared here (the variables needed by our program)
segment data use32 class=data
    numar dd 0
    len equ ($-numar)/4
    sir db '01234567', 0
    mesaj db "%c, ", 0
    rezultat times 10 db 0
    format db "%c", 0
    mesaj1 db " ",10, 0
    ok resb 1

; our code starts here
segment code use32 class=code
    modul:
        mov edi, [esp+4]
        mov eax, [esp+8]
        mov ebx, [esp+12]
        ;luam de pe stiva variabilele
        mov esi, 0; se va numara in esi numarul de resturi ale numarului in urma impartirii cu 2
        lop:
            mov dx, 0 ;dx:ax=numar
            mov cx, 8
            div cx ; dx=rest, ax=cat
            push ax
            mov eax, 0
            mov al, dl        
            xlat
            stosb
            inc esi
            pop ax
            cmp ax, 0
            jz afara
        jmp lop
        afara:
        ret 3*4
    start:
    mov ecx, 32
    _numere:
        mov [numar], ecx 
        push ecx; salvam ecx
        push dword [numar]
        push dword mesaj
        call [printf]
        add esp, 4*2; afisam numarul in baza 16
        
        mov ebx, sir
        mov eax, [numar]
        mov edi, rezultat
        
        push ebx
        push eax
        push edi; in ebx punem sirul format din 0 si 1, in eax numarul nostru, iar in edi offsetul sirului unde punem caractarele specifice resturilor lui 2
        call modul
        
        afis:
            mov eax, 0
            mov al, [rezultat+esi]; luam restul
            push eax
            push dword format
            call [printf]
            add esp, 4*2; il afisam
            cmp esi, 0
            jz afaraaa; cand se termina de luat resturile se iese din loop
            dec esi
            jmp afis
        afaraaa:
        push dword mesaj1
        call [printf]
        add esp, 4 ; format de spatiere
        pop ecx; readucem ecx ul pentru citirea numerelor din sir
        add ecx, 1
        cmp ecx, 127
        je final; daca nu mai sunt numere se iese
    jmp _numere
    
        final:
        push    dword 0      ; push the parameter for exit onto the stack
        call    [exit]       ; call exit to terminate the program