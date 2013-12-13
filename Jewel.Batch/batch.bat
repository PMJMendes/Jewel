@echo off
setlocal ENABLEDELAYEDEXPANSION
if defined CLASSPATH (set CLASSPATH=%CLASSPATH%;.) else (set CLASSPATH=.)
FOR /R . %%G IN (*.jar) DO set CLASSPATH=!CLASSPATH!;%%G
java Jewel.Batch.JewelBatchRunner
