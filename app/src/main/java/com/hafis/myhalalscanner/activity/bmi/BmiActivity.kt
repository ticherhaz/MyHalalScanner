package com.hafis.myhalalscanner.activity.bmi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hafis.myhalalscanner.R
import com.hafis.myhalalscanner.databinding.ActivityBmiBinding

class BmiActivity : AppCompatActivity() {

    private lateinit var viewModel: BmiViewModel
    private lateinit var binding: ActivityBmiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Init viewModel
        viewModel = ViewModelProvider(this).get(BmiViewModel::class.java)

        viewModel.calculate.observe(this, { result ->
            binding.tvResult.text = result.toString()
            if (result < 18.5) {
                binding.svBmi.setBackgroundColor(resources.getColor(R.color.yellow_300, null))
                binding.tvResultText.text = ResultBMI.UNDERWEIGHT.value
            } else if (result in 18.5..25.0) {
                binding.svBmi.setBackgroundColor(resources.getColor(R.color.green_300, null))
                binding.tvResultText.text = ResultBMI.NORMAL_WEIGHT.value
            } else if (result > 25 && result <= 30) {
                binding.svBmi.setBackgroundColor(resources.getColor(R.color.orange_300, null))
                binding.tvResultText.text = ResultBMI.OVERWEIGHT.value
            } else {
                binding.svBmi.setBackgroundColor(resources.getColor(R.color.red_300, null))
                binding.tvResultText.text = ResultBMI.OBESE.value
            }
        })

        setOnBtnCalculate()
    }

    private fun setOnBtnCalculate() {
        binding.btnCalculate.setOnClickListener {
            val weight = binding.etWeight.text.toString()
            val height = binding.etHeight.text.toString()

            if (weight.isNotEmpty() && height.isNotEmpty()) {
                viewModel.calculate(weight = weight.toDouble(), height = height.toDouble())
            }
        }
    }

    private enum class ResultBMI(val value: String) {
        UNDERWEIGHT("Underweight"),
        NORMAL_WEIGHT("Normal Weight"),
        OVERWEIGHT("Overweight"),
        OBESE("Obese");
    }
}