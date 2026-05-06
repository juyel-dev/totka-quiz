package com.juyel.totka.auth

import android.net.Uri
import androidx.lifecycle.ViewModel

class SignupViewModel : ViewModel() {
    // Page 1
    var gmail    = ""
    var password = ""

    // Page 2
    var fullName       = ""
    var profilePicUri: Uri? = null

    // Page 3
    var username   = ""
    var bio        = ""
    var board      = ""
    var classVal   = ""
    var phone      = ""
    var favSubject = ""
    var facebook   = ""
    var instagram  = ""
    var youtube    = ""
    var telegram   = ""
}
