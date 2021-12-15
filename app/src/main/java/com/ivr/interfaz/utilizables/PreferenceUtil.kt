package com.ivr.interfaz.utils

import android.content.Context
import android.content.SharedPreferences
import com.ivr.interfaz.R

object PreferenceUtil {

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(
                context.getString(R.string.preferences_name),
                Context.MODE_PRIVATE
        )
    }

    /*fun saveResultPreliminarySample(context: Context, result: Int) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
        with(editor) {
            putInt(context.getString(R.string.result_preliminary_sample), result)
            commit()
            apply()
        }
    }

    fun getResultPreliminarySample(context: Context): Int {
        val prefs = getPrefs(context)
        return prefs.getInt(context.getString(R.string.result_preliminary_sample), 80)
    }
    fun saveResultPreliminaryDias(context: Context, result: Int) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
        with(editor) {
            putInt(context.getString(R.string.result_preliminary_dias), result)
            commit()
            apply()
        }
    }


    fun getResultPreliminaryCliente(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(context.getString(R.string.result_preliminary_cliente), false)
    }
    fun saveResultPreliminaryCliente(context: Context, result: Boolean) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
        with(editor) {
            putBoolean(context.getString(R.string.result_preliminary_cliente), result)
            commit()
            apply()
        }
    }*/

    fun saveResultIdE(context: Context, result: String) {
        val prefs = getPrefs(context)
        val editor = prefs.edit();
        with(editor) {
            putString(context.getString(R.string.result_ide), result)
            commit()
            apply()
        }
    }
    fun getResultIdE(context: Context): String? {
        val prefs = getPrefs(context)
        return prefs.getString(context.getString(R.string.result_ide), "");
    }

    fun getResultRegistro(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(context.getString(R.string.result_registro), false)
    }

    fun saveResultRegistro(context: Context, result: Boolean) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
        with(editor) {
            putBoolean(context.getString(R.string.result_registro), result)
            commit()
            apply()
        }
    }

    /*fun getResultPreliminaryCliente(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(context.getString(R.string.result_preliminary_cliente), false)
    }
    fun saveResultPreliminaryCliente(context: Context, result: Boolean) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
        with(editor) {
            putBoolean(context.getString(R.string.result_preliminary_cliente), result)
            commit()
            apply()
        }
    }*/


    /*fun saveInactivePreliminaryUno(context: Context, result: Float) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
        with(editor) {
            putFloat(context.getString(R.string.inactive_preliminary_uno), result)
            apply()
        }
    }
    fun getInactivePreliminaryUno(context: Context): Float {
        val prefs = getPrefs(context)
        return prefs.getFloat(context.getString(R.string.inactive_preliminary_uno), 0.0F)
    }*/


}
