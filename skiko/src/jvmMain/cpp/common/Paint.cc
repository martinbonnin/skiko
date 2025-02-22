#include <iostream>
#include <jni.h>
#include "SkColorFilter.h"
#include "SkImageFilter.h"
#include "SkMaskFilter.h"
#include "SkPaint.h"
#include "SkPathEffect.h"
#include "SkShader.h"
#include "interop.hh"

static void deletePaint(SkPaint* paint) {
    delete paint;
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt_Paint_1nGetFinalizer
  (JNIEnv* env, jclass jclass) {
    return static_cast<jlong>(reinterpret_cast<uintptr_t>(&deletePaint));
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt_Paint_1nMake
  (JNIEnv* env, jclass jclass) {
    SkPaint* obj = new SkPaint();
    obj->setAntiAlias(true);
    return reinterpret_cast<jlong>(obj);
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt_Paint_1nMakeClone
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkPaint* obj = new SkPaint(*instance);
    return reinterpret_cast<jlong>(obj);
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_jetbrains_skia_PaintKt_Paint_1nEquals
  (JNIEnv* env, jclass jclass, jlong aPtr, jlong bPtr) {
    SkPaint* a = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(aPtr));
    SkPaint* b = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(bPtr));
    return *a == *b;
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt_Paint_1nReset
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->reset();
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_jetbrains_skia_PaintKt__1nIsAntiAlias
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return instance->isAntiAlias();
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetAntiAlias
  (JNIEnv* env, jclass jclass, jlong ptr, jboolean value) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setAntiAlias(value);
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_jetbrains_skia_PaintKt__1nIsDither
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return instance->isDither();
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetDither
  (JNIEnv* env, jclass jclass, jlong ptr, jboolean value) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setDither(value);
}

extern "C" JNIEXPORT jint JNICALL Java_org_jetbrains_skia_PaintKt_Paint_1nGetColor
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return instance->getColor();
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetColor
  (JNIEnv* env, jclass jclass, jlong ptr, jint color) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setColor(color);
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nGetColor4f
  (JNIEnv* env, jclass jclass, jlong ptr, jfloatArray result) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkColor4f color = instance->getColor4f();
    float data[4] { color.fR, color.fG, color.fB, color.fA };
    env->SetFloatArrayRegion(result, 0, 4, data);
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetColor4f
  (JNIEnv* env, jclass jclass, jlong ptr, jfloat r, jfloat g, jfloat b, jfloat a, jlong colorSpacePtr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkColorSpace* colorSpace = reinterpret_cast<SkColorSpace*>(static_cast<uintptr_t>(colorSpacePtr));
    instance->setColor4f({r, g, b, a}, colorSpace);
}

extern "C" JNIEXPORT jint JNICALL Java_org_jetbrains_skia_PaintKt__1nGetMode
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return static_cast<jlong>(instance->getStyle());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetMode
  (JNIEnv* env, jclass jclass, jlong ptr, jint mode) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setStyle(static_cast<SkPaint::Style>(mode));
}

extern "C" JNIEXPORT jfloat JNICALL Java_org_jetbrains_skia_PaintKt__1nGetStrokeWidth
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return instance->getStrokeWidth();
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetStrokeWidth
  (JNIEnv* env, jclass jclass, jlong ptr, jfloat width) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setStrokeWidth(width);
}

extern "C" JNIEXPORT jfloat JNICALL Java_org_jetbrains_skia_PaintKt__1nGetStrokeMiter
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return instance->getStrokeMiter();
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetStrokeMiter
  (JNIEnv* env, jclass jclass, jlong ptr, jfloat miter) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setStrokeMiter(miter);
}

extern "C" JNIEXPORT jint JNICALL Java_org_jetbrains_skia_PaintKt__1nGetStrokeCap
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return static_cast<jlong>(instance->getStrokeCap());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetStrokeCap
  (JNIEnv* env, jclass jclass, jlong ptr, jint cap) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setStrokeCap(static_cast<SkPaint::Cap>(cap));
}

extern "C" JNIEXPORT jint JNICALL Java_org_jetbrains_skia_PaintKt__1nGetStrokeJoin
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return static_cast<jlong>(instance->getStrokeJoin());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetStrokeJoin
  (JNIEnv* env, jclass jclass, jlong ptr, jint join) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setStrokeJoin(static_cast<SkPaint::Join>(join));
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt__1nGetMaskFilter
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return reinterpret_cast<jlong>(instance->refMaskFilter().release());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetMaskFilter
  (JNIEnv* env, jclass jclass, jlong ptr, jlong filterPtr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkMaskFilter* filter = reinterpret_cast<SkMaskFilter*>(static_cast<uintptr_t>(filterPtr));
    instance->setMaskFilter(sk_ref_sp<SkMaskFilter>(filter));
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt__1nGetImageFilter
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return reinterpret_cast<jlong>(instance->refImageFilter().release());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetImageFilter
  (JNIEnv* env, jclass jclass, jlong ptr, jlong filterPtr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkImageFilter* filter = reinterpret_cast<SkImageFilter*>(static_cast<uintptr_t>(filterPtr));
    instance->setImageFilter(sk_ref_sp<SkImageFilter>(filter));
}

extern "C" JNIEXPORT jint JNICALL Java_org_jetbrains_skia_PaintKt__1nGetBlendMode
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return static_cast<jlong>(instance->getBlendMode_or(SkBlendMode::kSrcOver));
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetBlendMode
  (JNIEnv* env, jclass jclass, jlong ptr, jint mode) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    instance->setBlendMode(static_cast<SkBlendMode>(mode));
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt__1nGetPathEffect
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return reinterpret_cast<jlong>(instance->refPathEffect().release());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetPathEffect
  (JNIEnv* env, jclass jclass, jlong ptr, jlong pathEffectPtr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkPathEffect* pathEffect = reinterpret_cast<SkPathEffect*>(static_cast<uintptr_t>(pathEffectPtr));
    instance->setPathEffect(sk_ref_sp(pathEffect));
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt__1nGetShader
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return reinterpret_cast<jlong>(instance->refShader().release());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetShader
  (JNIEnv* env, jclass jclass, jlong ptr, jlong shaderPtr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkShader* shader = reinterpret_cast<SkShader*>(static_cast<uintptr_t>(shaderPtr));
    instance->setShader(sk_ref_sp<SkShader>(shader));
}

extern "C" JNIEXPORT jlong JNICALL Java_org_jetbrains_skia_PaintKt__1nGetColorFilter
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return reinterpret_cast<jlong>(instance->refColorFilter().release());
}

extern "C" JNIEXPORT void JNICALL Java_org_jetbrains_skia_PaintKt__1nSetColorFilter
  (JNIEnv* env, jclass jclass, jlong ptr, jlong colorFilterPtr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    SkColorFilter* colorFilter = reinterpret_cast<SkColorFilter*>(static_cast<uintptr_t>(colorFilterPtr));
    instance->setColorFilter(sk_ref_sp<SkColorFilter>(colorFilter));
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_jetbrains_skia_PaintKt__1nHasNothingToDraw
  (JNIEnv* env, jclass jclass, jlong ptr) {
    SkPaint* instance = reinterpret_cast<SkPaint*>(static_cast<uintptr_t>(ptr));
    return instance->nothingToDraw();
}
