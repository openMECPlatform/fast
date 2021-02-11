#dpkg -s libboost-dev | grep 'Version'

#sudo apt-get autoremove libboost-dev

echo "Clone boost 1.58"

wget -O boost_1_58_0.tar.gz https://sourceforge.net/projects/boost/files/boost/1.58.0/boost_1_58_0.tar.gz/download

tar xvzf boost_1_58_0.tar.gz

sudo apt-get update -y

echo "Install dependent libraries for boost..."

sudo apt-get install cmake aptitude -y

sudo apt-get autoremove libboost-dev -y

sudo aptitude install build-essential cmake python-dev autotools-dev libicu-dev libbz2-dev libpcap-dev libssl1.0-dev -y

sudo apt-get install flex bison libbison-dev libmagic-dev swig -y

echo "Reinstall gcc on ubuntu 18.04..."
sudo apt-get autoremove gcc -y
sudo apt install g++-5 gcc-5 -y

echo "Update symlinks pointed to newly gcc..."

sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-5 10
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-5 20
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-5 10
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-5 20
sudo update-alternatives --install /usr/bin/cc cc /usr/bin/gcc 30
sudo update-alternatives --set cc /usr/bin/gcc
sudo update-alternatives --install /usr/bin/c++ c++ /usr/bin/g++ 30
sudo update-alternatives --set c++ /usr/bin/g++

ech "Check whether newly gcc is installed..."

gcc --version

echo "Start installing boost..."

cd boost_1_58_0

./bootstrap.sh --prefix=/usr/

echo "Building boost..."

./b2

echo "Finally install boost..."

sudo ./b2 install

echo "Finish installing boost..."

echo "Install bro 2.1"

cd ..

git clone --recursive https://github.com/bro/bro.git -b v2.1

cp ../fast_bro.patch bro/

echo "Merge fast bro patch..."

cd bro

patch -p1 < fast_bro.patch

echo "Start to configure bro..."

./configure
