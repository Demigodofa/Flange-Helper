package com.kevin.flangejointassembly.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class JobStorageRoundTripTest {
    @Test
    fun roundTripPreservesSymbols() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val form = FlangeFormItem(
            id = "form-1",
            jobId = "job-1",
            dateMillis = 1700000000000,
            description = "Pump @ Area #5 (A&B) | Line-1",
            serviceType = "Svc-Type: A+B/C",
            gasketType = "Soft cut sheet (<= 1/16 in)",
            wrenchSerials = "SN#1234-AB/CD",
            wrenchCalDateMillis = 1700000000000,
            torqueDry = true,
            torqueWet = false,
            lubricantType = "Unlubricated (K 0.27)",
            flangeClass = "600#",
            pipeSize = "12\" Sch-40",
            customInnerDiameter = "5.0",
            customOuterDiameter = "10.0",
            customThickness = "1.25",
            flangeFace = "RF",
            boltHoles = "16",
            flangeFaceCondition = "Yes",
            flangeParallel = "Yes",
            fastenerType = "Studs",
            fastenerSpec = "A193 B7",
            fastenerClass = "",
            fastenerLength = "12.5",
            fastenerDiameter = "1-1/8",
            threadSeries = "8UN",
            nutSpec = "A194 2H",
            nutOverrideAcknowledged = false,
            washerUsed = true,
            workingTempF = "600",
            roundedTempF = "600",
            torqueMethod = "YIELD_PERCENT",
            targetBoltLoadF = "12345",
            pctYieldTarget = "0.50",
            tpiUsed = "8",
            asUsed = "0.790",
            strengthKsiUsed = "105",
            kUsed = "0.27",
            calculatedTargetTorque = "1500",
            specifiedTargetTorque = "",
            pass1Confirmed = true,
            pass1Initials = "AB",
            pass2Confirmed = true,
            pass2Initials = "CD",
            pass3Confirmed = false,
            pass3Initials = "",
            pass4Confirmed = false,
            pass4Initials = "",
            photoUris = listOf("content://com.kevin.flangejointassembly.fileprovider/photos/test_#1.png"),
            contractorPrintName = "Name #1 (A&B)",
            contractorSignUri = "content://com.kevin.flangejointassembly.fileprovider/signatures/sig_1.png",
            contractorDateMillis = 1700000000000,
            facilityPrintName = "Facility/Rep-1",
            facilitySignUri = "content://com.kevin.flangejointassembly.fileprovider/signatures/sig_2.png",
            facilityDateMillis = 1700000000000
        )

        val job = JobItem(
            id = "job-1",
            number = "JOB#1/2-TEST",
            location = "Plant A > Unit B | Line 3",
            dateMillis = 1700000000000,
            flangeForms = listOf(form)
        )

        JobStorage.saveJobs(context, listOf(job))
        val loaded = JobStorage.loadJobs(context)

        assertEquals(1, loaded.size)
        val loadedJob = loaded.first()
        assertEquals(job.number, loadedJob.number)
        assertEquals(job.location, loadedJob.location)
        assertEquals(1, loadedJob.flangeForms.size)
        val loadedForm = loadedJob.flangeForms.first()
        assertEquals(form.description, loadedForm.description)
        assertEquals(form.serviceType, loadedForm.serviceType)
        assertEquals(form.wrenchSerials, loadedForm.wrenchSerials)
        assertEquals(form.contractorPrintName, loadedForm.contractorPrintName)
        assertEquals(form.facilityPrintName, loadedForm.facilityPrintName)

        cleanup(context)
    }

    private fun cleanup(context: android.content.Context) {
        val root = File(context.filesDir, "flange_helper")
        root.deleteRecursively()
    }
}
