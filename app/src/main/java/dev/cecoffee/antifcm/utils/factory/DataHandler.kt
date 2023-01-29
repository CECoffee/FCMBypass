package dev.cecoffee.antifcm.utils.factory

class DataHandler {
    companion object{
        var uidDataList = mutableListOf<String>()
        var cancelHook = false
        var dialogDisplayed = false
        var selectedData = ""
    }
}