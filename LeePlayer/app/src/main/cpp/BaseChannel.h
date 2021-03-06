//
// Created by jv.lee on 2019-06-05.
//

#ifndef LEEPLAYER_BASECHANNEL_H
#define LEEPLAYER_BASECHANNEL_H

extern "C" {
#include <libavcodec/avcodec.h>
};

#include "safe_queue.h"
#include "JavaCallHelper.h"


class BaseChannel {
public:
    BaseChannel(int id, JavaCallHelper *javaCallHelper, AVCodecContext *avCodecContext,AVRational time_base)
            : channelID(id), javaCallHelper(javaCallHelper), avCodecContext(avCodecContext),time_base(time_base) {
        //添加解决方案 是否解决未知
        packet_queue.setReleaseHandle(releaseAvPacket);
        frame_queue.setReleaseHandle(releaseAvFrame);
    };

    virtual ~BaseChannel() {
        if (avCodecContext) {
            avcodec_close(avCodecContext);
            avcodec_free_context(&avCodecContext);
            avCodecContext = 0;
        }
        packet_queue.clear();
        frame_queue.clear();
        LOGE("释放channel:%d %d", packet_queue.size(), frame_queue.size());
    };

    //清空队列
    void clear(){
        packet_queue.clear();
        frame_queue.clear();
    }

    void startWork(){
        packet_queue.setWork(1);
        frame_queue.setWork(1);
    }

    void stopWork(){
        packet_queue.setWork(0);
        frame_queue.setWork(0);
    }

    /**
     * 释放packet
     * @param packet
     */
    static void releaseAvPacket(AVPacket *&packet) {
        if (packet) {
            av_packet_free(&packet);
            packet = 0;
        }
    }

    /**
     * 释放frame
     * @param frame
     */
    static void releaseAvFrame(AVFrame *&frame) {
        if (frame) {
            av_frame_free(&frame);
            frame = 0;
        }
    }

    virtual void play() = 0;
    virtual void stop() = 0;
    SafeQueue<AVPacket *> packet_queue;
    SafeQueue<AVFrame *> frame_queue;
    volatile int channelID;
    volatile bool isPlaying;
    AVCodecContext *avCodecContext;
    JavaCallHelper *javaCallHelper;
    AVRational time_base;
    double clock = 0;
};


#endif //LEEPLAYER_BASECHANNEL_H
