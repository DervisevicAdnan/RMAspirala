package com.example.projekatspirale

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ButtonErrorMatcher(private val expectedErrorText: String) : androidx.test.espresso.matcher.BoundedMatcher<View, Button>(Button::class.java) {

    override fun matchesSafely(item: Button): Boolean {
        val hasErrorText = item.error.toString().equals(expectedErrorText)
        return hasErrorText
    }

    override fun describeTo(description: Description) {
        description.appendText("with error text: '$expectedErrorText'")
    }
}


class ButtonNoErrorMatcher() : androidx.test.espresso.matcher.BoundedMatcher<View, Button>(Button::class.java) {

    override fun matchesSafely(item: Button): Boolean {
        val hasErrorText = (item.error == null)
        return hasErrorText
    }

    override fun describeTo(description: Description) {
        description.appendText("with no error")
    }
}

class EditTextNoErrorMatcher() : androidx.test.espresso.matcher.BoundedMatcher<View, EditText>(EditText::class.java) {

    override fun matchesSafely(item: EditText): Boolean {
        val hasErrorText = (item.error == null)
        return hasErrorText
    }

    override fun describeTo(description: Description) {
        description.appendText("with no error")
    }
}
class withDrawable(@DrawableRes private val id: Int) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("ImageView with drawable same as drawable with id $id")
    }

    override fun matchesSafely(view: View): Boolean {
        val context = view.context
        val expectedBitmap = context.getDrawable(id)?.toBitmap()

        return view is ImageView && view.drawable.toBitmap().sameAs(expectedBitmap)
    }
}

@RunWith(AndroidJUnit4::class)
class Spirala2InstrumentedTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testValidiraSvaPolja() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(ButtonErrorMatcher("Nije selektovan item u svim listama\nNije dodano ni jedno jelo")))
        onView(withId(R.id.nazivET)).check(matches(hasErrorText("Dužina naziva biljke mora biti između 2 i 20 karaktera")))
        onView(withId(R.id.porodicaET)).check(matches(hasErrorText("Dužina naziva porodice mora biti između 2 i 20 karaktera")))
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(hasErrorText("Dužina medicinskog upozorenja mora biti između 2 i 20 karaktera")))
        onView(withId(R.id.jeloET)).check(matches(not(hasErrorText("Dužina naziva biljke mora biti između 2 i 20 karaktera"))))
    }

    @Test
    fun testValidiraNazivET() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.nazivET)).check(matches(hasErrorText("Dužina naziva biljke mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.nazivET)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("a"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.nazivET)).check(matches(hasErrorText("Dužina naziva biljke mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.nazivET)).perform(click())
        onView(withId(R.id.nazivET)).perform(replaceText(""))
        onView(withId(R.id.nazivET)).perform(typeText("aaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.nazivET)).check(matches(hasErrorText("Dužina naziva biljke mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.nazivET)).perform(click())
        onView(withId(R.id.nazivET)).perform(replaceText(""))
        onView(withId(R.id.nazivET)).perform(typeText("aa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.nazivET)).check(matches(EditTextNoErrorMatcher()))

        onView(withId(R.id.nazivET)).perform(click())
        onView(withId(R.id.nazivET)).perform(replaceText(""))
        onView(withId(R.id.nazivET)).perform(typeText("aaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.nazivET)).check(matches(EditTextNoErrorMatcher()))
    }

    @Test
    fun testValidiraPorodicaET() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.porodicaET)).check(matches(hasErrorText("Dužina naziva porodice mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.porodicaET)).perform(click())
        onView(withId(R.id.porodicaET)).perform(typeText("a"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.porodicaET)).check(matches(hasErrorText("Dužina naziva porodice mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.porodicaET)).perform(click())
        onView(withId(R.id.porodicaET)).perform(replaceText(""))
        onView(withId(R.id.porodicaET)).perform(typeText("aaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.porodicaET)).check(matches(hasErrorText("Dužina naziva porodice mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.porodicaET)).perform(click())
        onView(withId(R.id.porodicaET)).perform(replaceText(""))
        onView(withId(R.id.porodicaET)).perform(typeText("aa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.porodicaET)).check(matches(EditTextNoErrorMatcher()))

        onView(withId(R.id.porodicaET)).perform(click())
        onView(withId(R.id.porodicaET)).perform(replaceText(""))
        onView(withId(R.id.porodicaET)).perform(typeText("aaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.porodicaET)).check(matches(EditTextNoErrorMatcher()))
    }

    @Test
    fun testValidiraMedicinskoUpozorenjeET() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(hasErrorText("Dužina medicinskog upozorenja mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.medicinskoUpozorenjeET)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("a"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(hasErrorText("Dužina medicinskog upozorenja mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.medicinskoUpozorenjeET)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(replaceText(""))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("aaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(hasErrorText("Dužina medicinskog upozorenja mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.medicinskoUpozorenjeET)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(replaceText(""))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("aa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(EditTextNoErrorMatcher()))

        onView(withId(R.id.medicinskoUpozorenjeET)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(replaceText(""))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("aaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(EditTextNoErrorMatcher()))
    }

    @Test
    fun testValidiraJeloET() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())

        onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("a"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Dužina naziva jela mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText(""))
        onView(withId(R.id.jeloET)).perform(typeText("aaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Dužina naziva jela mora biti između 2 i 20 karaktera")))

        onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText(""))
        onView(withId(R.id.jeloET)).perform(typeText("aa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(EditTextNoErrorMatcher()))

        onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("aa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Jelo već postoji u listi")))

        onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText("AA"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Jelo već postoji u listi")))

        onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText(""))
        onView(withId(R.id.jeloET)).perform(typeText("aaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(EditTextNoErrorMatcher()))
    }

    @Test
    fun testValidneListe(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(ButtonErrorMatcher("Nije selektovan item u svim listama\nNije dodano ni jedno jelo")))
        onView(withId(R.id.nazivET)).perform(click())
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onData(anything()).inAdapterView(withId(R.id.medicinskaKoristLV)).atPosition(0).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(ButtonErrorMatcher("Nije selektovan item u svim listama\nNije dodano ni jedno jelo")))
        onView(withId(R.id.nazivET)).perform(click())
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onData(anything()).inAdapterView(withId(R.id.profilOkusaLV)).atPosition(0).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(ButtonErrorMatcher("Nije selektovan item u svim listama\nNije dodano ni jedno jelo")))
        onView(withId(R.id.nazivET)).perform(click())
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onData(anything()).inAdapterView(withId(R.id.zemljisniTipLV)).atPosition(0).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(ButtonErrorMatcher("Nije selektovan item u svim listama\nNije dodano ni jedno jelo")))
        onView(withId(R.id.nazivET)).perform(click())
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())

        onData(anything()).inAdapterView(withId(R.id.klimatskiTipLV)).atPosition(0).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(ButtonErrorMatcher("Nije dodano ni jedno jelo")))
    }

    @Test
    fun testIzmjenaJela(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())

        //onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("aa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        //onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("aaa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onData(anything()).inAdapterView(withId(R.id.jelaLV)).atPosition(0).perform(click())

        onView(withId(R.id.dodajJeloBtn)).check(matches(withText("Izmijeni jelo")))
        onView(withId(R.id.jeloET)).check(matches(withText("aa")))

        //onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText("aaa"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Jelo već postoji u listi")))

        //onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText("a"))
        onView(withId(R.id.nazivET)).perform(click())
        //onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Dužina naziva jela mora biti između 2 i 20 karaktera")))

        //onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText("aaaaaaaaaaaaaaaaaaaaa"))
        onView(withId(R.id.nazivET)).perform(click())
        //onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Dužina naziva jela mora biti između 2 i 20 karaktera")))

        //onView(withId(R.id.jeloET)).perform(click())
        onView(withId(R.id.jeloET)).perform(replaceText("aaaaaaa"))
        onView(withId(R.id.nazivET)).perform(click())
        //onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.dodajJeloBtn)).check(matches(withText("Dodaj jelo")))
    }

    @Test
    fun testDodavanjeBiljke(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Naziv"))
        onView(withId(R.id.porodicaET)).perform(typeText("Porodica"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("Upozorenje"))
        onView(withId(R.id.jeloET)).perform(typeText("Jelo1"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("Jelo2"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onData(anything()).inAdapterView(withId(R.id.medicinskaKoristLV)).atPosition(0).perform(click())
        onData(anything()).inAdapterView(withId(R.id.medicinskaKoristLV)).atPosition(1).perform(click())
        onData(anything()).inAdapterView(withId(R.id.medicinskaKoristLV)).atPosition(2).perform(click())
        onData(anything()).inAdapterView(withId(R.id.profilOkusaLV)).atPosition(0).perform(click())
        onData(anything()).inAdapterView(withId(R.id.zemljisniTipLV)).atPosition(0).perform(click())
        onData(anything()).inAdapterView(withId(R.id.zemljisniTipLV)).atPosition(1).perform(click())
        onData(anything()).inAdapterView(withId(R.id.zemljisniTipLV)).atPosition(2).perform(click())
        onData(anything()).inAdapterView(withId(R.id.klimatskiTipLV)).atPosition(0).perform(click())
        onData(anything()).inAdapterView(withId(R.id.klimatskiTipLV)).atPosition(1).perform(click())
        onData(anything()).inAdapterView(withId(R.id.klimatskiTipLV)).atPosition(2).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())

        onView(withId(R.id.biljkeRV)).perform(swipeUp())
        onView(withId(R.id.biljkeRV)).perform(swipeUp())
        onView(withId(R.id.biljkeRV)).perform(swipeUp())
        onView(withText("Naziv")).check(matches(isDisplayed()))
        onView(withText("Upozorenje")).check(matches(isDisplayed()))
    }
    @Test
    fun testSlikanja(){
        Intents.init()
        val bundle = Bundle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //Log.e("test1", (context==null).toString())
        val drawable = ContextCompat.getDrawable(context,R.drawable.test)
        //Log.e("test1", (context==null).toString())
        val imageBitmap = (drawable as BitmapDrawable).bitmap

        //Log.e("test2", (imageBitmap==null).toString())
        bundle.putParcelable("data", imageBitmap)

        val resultData = Intent()
        resultData.putExtras(bundle)
        //Log.e("test3", resultData.toString())

        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        //Log.e("teeeeest", result.toString())
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.uslikajBiljkuBtn)).perform(click())

        onView(withId(R.id.slikaIV)).check(matches(withDrawable(R.drawable.test)))
        onView(withId(R.id.nazivET)).perform(typeText("Naziv"))
        onView(withId(R.id.porodicaET)).perform(typeText("Porodica"))
        Intents.release()
    }


}