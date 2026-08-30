"use client";

import { useState } from "react";
import { ArrowRight, GitBranch, Play, RotateCcw, Box, CreditCard, ShoppingCart, Truck } from "lucide-react";
import Link from "next/link";
import CausalGraph from "@/components/CausalGraph";

export default function TimelineView({ params }: { params: { id: string } }) {
  const [showForkModal, setShowForkModal] = useState(false);
  const isExperiment = params.id === "exp-001";

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <div className="flex items-center justify-between mb-8 pb-4 border-b border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight flex items-center">
            {isExperiment ? "Payment Failure Experiment" : "INC-001: Inventory Timeout"}
            {isExperiment && (
              <span className="ml-3 px-2 py-0.5 bg-amber-500/10 text-amber-400 rounded text-xs border border-amber-500/20">
                EXP-001
              </span>
            )}
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            {isExperiment ? "Forked from E-Commerce Demo at Event #101" : "Main execution timeline with distributed failure"}
          </p>
        </div>
        <div className="flex space-x-3">
          {!isExperiment && (
            <button
              onClick={() => setShowForkModal(true)}
              className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors text-sm font-medium"
            >
              <GitBranch size={16} />
              <span>Fork Timeline</span>
            </button>
          )}
        </div>
      </div>

      <div className="flex-1 overflow-hidden">
        <CausalGraph />
      </div>

      {/* Fork Modal */}
      {showForkModal && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-700 rounded-2xl w-full max-w-md p-6 shadow-2xl">
            <h2 className="text-xl font-bold text-white mb-6">Create Experiment (Fork)</h2>
            <div className="space-y-4 mb-8">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-1">Experiment Name</label>
                <input type="text" defaultValue="What if timeout never occurred?" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-1">Fork From Event</label>
                <div className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-slate-300 font-mono text-sm">
                  evt-4 (INVENTORY_RESERVATION_REQUESTED)
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2 mt-4">Simulate Alternate Outcome</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
                  <option>Inventory Service: SUCCESS (No Timeout)</option>
                  <option>Payment Service: FAILED</option>
                  <option>Network: DELAY 500ms</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowForkModal(false)} className="px-4 py-2 text-slate-300 hover:text-white font-medium transition-colors">Cancel</button>
              <Link href="/timeline/exp-001" onClick={() => setShowForkModal(false)}>
                <button className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-lg shadow-lg shadow-indigo-500/20 transition-all">
                  Run Simulation
                </button>
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
