#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define ARRAY_MAX 256

int num_rander();

int main(int argc, char** argv)
{
    FILE* src_file;
    FILE* tmp_file;

    char ch;
    int ret = 0;
    if(argc != 2 )
    {
        printf("argc = %d\n", argc);
        printf("Error : Usage ./encryter [FILE]\n");
        exit(1);
    }
    if(argv[1] == NULL)
    {
        printf("Error : File name can not be empty!\n");
        exit(1);
    }   

    char str_file_name[ARRAY_MAX] = {0};
    char cmd_buf[ARRAY_MAX] = {0};

    sprintf(str_file_name, "%s", argv[1]);
    printf("File name is %s\n", str_file_name);
    //gets(str_file_name);

    src_file = fopen(str_file_name, "rb+");
    if(src_file == NULL)
    {
        printf("/n file:%s open error!/n", str_file_name);
    }

    tmp_file = fopen("temp.cry", "wb+");
    if(tmp_file == NULL)
    {
        printf("/n File create error!/n");
    }

    /*Encrypter every character, and write it to file*/
    while(!feof(src_file))
    {
        ret = fgetc(src_file);
        if(ret != EOF)
        {
            ch = (char)ret;
            ch = ~ch;
            fputc(ch, tmp_file);
        }
    }
    fclose(src_file);
    fclose(tmp_file);
    /*Delete old file*/
    sprintf(cmd_buf, "rm -rf %s", str_file_name);
    system(cmd_buf);

    bzero(cmd_buf, strlen(cmd_buf));
    sprintf(cmd_buf, "mv temp.cry %s", str_file_name);
    system(cmd_buf);

    //num_rander(10);
    return 0;
}

int num_rander(int rows)
{
    int num = 0;
    srand((unsigned)time(NULL));
    num = rand() % rows + 1;
    printf("Rand number is %d\n", num);
    return num;
}
