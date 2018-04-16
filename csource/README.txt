Compiling
gcc -fPIC -c readX.c

Creating a dynamic lib:
gcc -shared -o readX.so readX.o -lm

sudo cp readX.so /usr/lib/libreadX.so
sudo chmod 0755 /usr/lib/libreadX.so
sudo ldconfig
