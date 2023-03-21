#include <stdio.h>
#include <stdlib.h>

int get_signature(FILE *file) {
  fseek(file, 0x3C, SEEK_SET);
  int signature; 
  fread(&signature, sizeof(int), 1, file);
  return signature;
}

int is_pe(FILE *file) {
  char *symbols = (char*) malloc(4 * sizeof(char));
  fseek(file, get_signature(file), SEEK_SET);
  fread(symbols, sizeof(char), 4, file);
  fclose(file);
    
  if (symbols[0] == 'P' && symbols[1] == 'E' && symbols[2] == '\0' && symbols[3] == '\0') {
    free(symbols);
    printf("PE\n");
    return 0;
  } else {
    free(symbols);
    printf("Not PE\n");
    return 1;
  }  
}

void print_str(FILE *file, int start) {
  fseek(file, start, SEEK_SET);
  char ch;
  fread(&ch, sizeof(char), 1, file);
  while (ch != '\0') {
    printf("%c", ch);
    fread(&ch, sizeof(char), 1, file);
  }
  printf("\n");
}

int check_n_bytes(FILE *file, int start, int n) {
  fseek(file, start, SEEK_SET);
  char *dependence = (char*) malloc(n * sizeof(char));
  fread(dependence, sizeof(char), n, file);
  int count = 0;
  for (int i = 0; i < n; i++) {
    count += (dependence[i] != 0);
  }
  free(dependence);
  return count;
}

void import_functions(FILE *file) {
  int optional = get_signature(file) + 24;
  fseek(file, optional, SEEK_SET);
  fseek(file, 0x78, SEEK_CUR);
  int import_table_rva;
  fread(&import_table_rva, sizeof(int), 1, file);
    
  int section_virtual_size, section_rva, section_raw;
  int section = optional + 240;
  do {
    fseek(file, section, SEEK_SET);
    fseek(file, 0x8, SEEK_CUR);
    fread(&section_virtual_size, sizeof(int), 1, file);
    
    fseek(file, section, SEEK_SET);
    fseek(file, 0xC, SEEK_CUR);
    fread(&section_rva, sizeof(int), 1, file);
      
    fseek(file, section, SEEK_SET);
    fseek(file, 0x14, SEEK_CUR);
    fread(&section_raw, sizeof(int), 1, file);
      
    section += 40;
  } while (import_table_rva < section_rva || section_rva + section_virtual_size <= import_table_rva);
  int offset_rva2raw = section_raw - section_rva;
  int import_raw = import_table_rva + offset_rva2raw;
    
  for (int dependence_raw = import_raw; check_n_bytes(file, dependence_raw, 20); dependence_raw += 20) {
    int name_rva;
    fseek(file, dependence_raw, SEEK_SET);
    fseek(file, 0xC, SEEK_CUR);
    fread(&name_rva, sizeof(int), 1, file);
    int name_raw = name_rva + offset_rva2raw;
    print_str(file, name_raw);
      
    fseek(file, dependence_raw, SEEK_SET);
    int lookup_rva;
    fread(&lookup_rva, sizeof(int), 1, file);
    int lookup_raw = lookup_rva + offset_rva2raw;
    for (int image_raw = lookup_raw; check_n_bytes(file, image_raw, 8); image_raw += 8) {
      fseek(file, image_raw, SEEK_SET);
      long name_func_rva;
      fread(&name_func_rva, sizeof(long), 1, file);
      if (!(name_func_rva & 1)) {
        long name_func_raw = name_func_rva + offset_rva2raw + 2;
        printf("    ");
        print_str(file, name_func_raw);
      }
    }
  }
}

void export_functions(FILE *file) {
  int optional = get_signature(file) + 24;
  fseek(file, optional, SEEK_SET);
  fseek(file, 0x70, SEEK_CUR);
  int export_table_rva;
  fread(&export_table_rva, sizeof(int), 1, file);
  	
  int section_virtual_size, section_rva, section_raw;
  int section = optional + 240;
  do {
    fseek(file, section, SEEK_SET);
    fseek(file, 0x8, SEEK_CUR);
    fread(&section_virtual_size, sizeof(int), 1, file);
    
    fseek(file, section, SEEK_SET);
    fseek(file, 0xC, SEEK_CUR);
    fread(&section_rva, sizeof(int), 1, file);
      
    fseek(file, section, SEEK_SET);
    fseek(file, 0x14, SEEK_CUR);
    fread(&section_raw, sizeof(int), 1, file);
      
    section += 40;
  } while (export_table_rva < section_rva || section_rva + section_virtual_size <= export_table_rva);
  int offset_rva2raw = section_raw - section_rva;
  int export_raw = export_table_rva + offset_rva2raw;
  
  fseek(file, export_raw, SEEK_SET);
  fseek(file, 0x20, SEEK_CUR);
  int name_pointer_rva;
  fread(&name_pointer_rva, sizeof(int), 1, file);
  int name_pointer_raw = name_pointer_rva + offset_rva2raw;
  
  int num;
  fseek(file, export_raw, SEEK_SET);
  fseek(file, 0x18, SEEK_CUR);
  fread(&num, sizeof(int), 1, file);
  for (int i = 0; i < num; i++) {
    fseek(file, name_pointer_raw + i * 4, SEEK_SET);
    int address;
    fread(&address, sizeof(int), 1, file);
    address += offset_rva2raw;
    print_str(file, address);
  }
}

int main(int argc, char** argv) {
  if (argc == 3) {
    FILE *file;
    file = fopen(argv[2], "rb");
    if (strcmp(argv[1], "is-pe") == 0) {
      return is_pe(file);
    } else if (strcmp(argv[1], "import-functions") == 0) {
      import_functions(file);
    } else if (strcmp(argv[1], "export-functions") == 0) {
      export_functions(file);
    }
  }
  return 0;
}
