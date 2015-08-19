/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.proparprog

import java.util.{UUID, Random}
import java.util.concurrent.{ForkJoinPool, TimeUnit, Executors, ThreadPoolExecutor}
import java.util.stream.{Collectors, IntStream}

import com.google.common.base.Stopwatch
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scala.collection.parallel.ForkJoinTaskSupport
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

@RunWith(classOf[JUnitRunner])
class ParallelCollectionsSpec extends Specification {

  def slow1:Int = {
    Thread.sleep(300)
    123
  }

  def slow2:String = {
    Thread.sleep(700)
    "456"
  }

  def slow3:Long = {
    Thread.sleep(500)
    789
  }

  "Parallel Collections" should {

    "run in parallel" in {
      val strings = List("first", "second", "third")

      val processed = strings.par.map { s =>
        val sleep = s match {
          case "first" => 500
          case "second" => 1000
          case "third" => 100
        }
        Thread.sleep(sleep)
        println(s)
        s
      }.toList

      processed must beEqualTo(strings)
    }

    "run with custom fork join task support" in {

      // use a helper ExecutionContext.fromExecutor() instead of boiler plate code below
      // this is just a verbose example
      val stopwatch1 = Stopwatch.createStarted
      val random = new Random
      val uuids1 = Stream.range(0, 1000, 1).par.map((i) => {
          Thread.sleep(random.nextInt(100))
        UUID.randomUUID().toString()
      }).toList

      val duration1 = stopwatch1.elapsed(TimeUnit.MILLISECONDS)

      val stopwatch2 = Stopwatch.createStarted

      val par = Stream.range(0, 1000, 1).par
      par.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(Runtime.getRuntime.availableProcessors * 2))

      val uuids2 = par.map((i) => {
        Thread.sleep(random.nextInt(100))
        UUID.randomUUID().toString()
      }).toList

      val duration2: Long = stopwatch2.elapsed(TimeUnit.MILLISECONDS)

      println("parallel collection 1 ==> " + duration1 / 1000.toFloat)
      println("parallel collection 2 ==> " + duration2 / 1000.toFloat)

      uuids1 must haveSize(uuids2.size)
    }

  }

}
