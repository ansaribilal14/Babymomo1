# BabyMomo ProGuard Rules
-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod
-keep class com.babymomo.app.data.db.entities.** { *; }
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keep,includedescriptorclasses class com.babymomo.app.**$$serializer { *; }
-keepclassmembers class com.babymomo.app.** { *** Companion; *** INSTANCE; kotlinx.serialization.KSerializer serializer(...); }
-dontwarn com.google.dagger.**
-dontwarn io.ktor.**
-keep class net.sqlcipher.** { *; }
