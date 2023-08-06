package com.example.workinstructions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workinstructions.databinding.ActivityWidetailBinding
import com.example.workinstructions.model.Step
import com.example.workinstructions.model.WorkInstruction

class WIDetailActivity : AppCompatActivity() {

//    lateinit var nextBtn: Button
//    lateinit var prevBtn: Button
//    lateinit var allStepBtn: Button
//
//    lateinit var stepTv: TextView
//    lateinit var InstructionTv: TextView
//    lateinit var warningTv: TextView
//    lateinit var hintTv: TextView
//    lateinit var reactionPlanTv: TextView

    var mBinding: ActivityWidetailBinding?= null
    var stepList = ArrayList<Step>()
    var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWidetailBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)

        fetchSteps()

        mBinding?.nextBtn?.setOnClickListener{
            currentPosition++
            setStepData(stepList[currentPosition])
            updateButtonsVisibility(currentPosition)
        }

        mBinding?.prevBtn?.setOnClickListener{
            currentPosition--
            setStepData(stepList[currentPosition])
            updateButtonsVisibility(currentPosition)
        }
    }

    private fun setStepData(step: Step) {
        mBinding?.stepTv?.text = step.name
        mBinding?.instructionTv?.text = step.instruction
        mBinding?.warningTv?.text = step.warning
        mBinding?.hintTv?.text = step.hint
        mBinding?.reactionPlanTv?.text = step.reactionPlan
        mBinding?.wiImg?.setImageDrawable(getDrawable(R.drawable.worker_working))
    }

    private fun fetchSteps() {
        stepList = intent.getSerializableExtra("steps") as ArrayList<Step>
        if(!stepList.isNullOrEmpty()){
            setStepData(stepList[currentPosition])
        }
    }

    private fun updateButtonsVisibility(currentPosition: Int) {
        mBinding?.prevBtn?.isEnabled = currentPosition > 0
        mBinding?.nextBtn?.isEnabled = currentPosition < stepList.size - 1
    }
}