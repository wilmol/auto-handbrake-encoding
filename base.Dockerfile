# base HandBrake + Java image

FROM amazoncorretto:21.0.1-alpine3.15

# Compile HandBrake
# credit: https://github.com/txstate-etc/mediaflo-encoder/blob/77bda38311f97903bd11d4fe10a0c516e8d8d145/worker/Dockerfile
# TODO multistage build didn't work (after copying HandBrakeCLI, it complained about missing files)
RUN \
    apk add \
        # build tools.
        autoconf \
        automake \
        build-base \
        bzip2-dev \
        cmake \
        coreutils \
        diffutils \
        file \
        git \
        harfbuzz-dev \
        jansson-dev \
        lame-dev \
        libass-dev \
        libjpeg-turbo-dev \
        libsamplerate-dev \
        libtheora-dev \
        libtool \
        libxml2-dev \
        libva-dev \
        libvorbis-dev \
        libvpx-dev \
        linux-headers \
        m4 \
        meson \
        nasm \
        ninja \
        numactl-dev \
        opus-dev \
        patch \
        python3 \
        speex-dev \
        tar \
        x264-dev \
        xz-dev \
        yasm \
        && \
    # Download source
    git clone https://github.com/HandBrake/HandBrake && \
    cd HandBrake && \
    git checkout $(git describe --tags $(git rev-list --tags --max-count=1)) && \
    ./configure --disable-gtk \
                --enable-fdk-aac \
                --launch-jobs=$(nproc) \
                --launch
