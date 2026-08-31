"use client";

import { useState } from "react";
import { AlertTriangle, Plus, Power, ServerCrash, RotateCw, Activity, ShieldAlert } from "lucide-react";

export default function FaultInjectionConsole() {
  const [activeFaults, setActiveFaults] = useState<any[]>([
    {
      id: "FAULT-001",
      service: "inventory-service",
      type: "DROP",
      targetEvent: "INVENTORY_RESERVATION_REQUESTED",
      mode: "ONE_SHOT",
      status: "ACTIVE"
    }
  ]);

  return (
    <div className="flex h-full flex-col p-8 max-w-5xl mx-auto w-full">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white tracking-tight flex items-center">
          Fault Injection Console
          <span className="ml-4 px-2 py-0.5 bg-rose-500/10 text-rose-400 rounded text-sm border border-rose-500/20">Phase 7 Engine</span>
        </h1>
        <p className="text-slate-400 mt-2">
          Dynamically inject failures into the distributed cluster via the control plane.
        </p>
      </div>

      <div className="grid grid-cols-3 gap-8">
        {/* Configuration Panel */}
        <div className="col-span-2 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
          <h2 className="text-lg font-bold text-white flex items-center mb-6 pb-4 border-b border-slate-800">
            <Plus size={18} className="mr-2 text-indigo-400" /> Configure New Fault
          </h2>

          <div className="space-y-6">
            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Target Service</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 transition-colors">
                  <option>inventory-service</option>
                  <option>order-service</option>
                  <option>payment-service</option>
                  <option>shipping-service</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Target Event</label>
                <input type="text" defaultValue="INVENTORY_RESERVATION_REQUESTED" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white font-mono text-sm focus:outline-none focus:border-indigo-500 transition-colors" />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Fault Type</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 transition-colors">
                  <option value="DROP">DROP (Prevent processing)</option>
                  <option value="LATENCY">LATENCY (Delay processing)</option>
                  <option value="DUPLICATE">DUPLICATE (Process twice)</option>
                  <option value="CRASH">CRASH (Refuse processing)</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Duration (ms)</label>
                <input type="number" defaultValue={0} className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 transition-colors disabled:opacity-50" disabled />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Execution Mode</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 transition-colors">
                  <option value="ONE_SHOT">ONE_SHOT</option>
                  <option value="PERSISTENT">PERSISTENT</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Probability</label>
                <div className="flex items-center">
                  <input type="number" defaultValue={1.0} step={0.1} max={1.0} min={0.0} className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white focus:outline-none focus:border-indigo-500 transition-colors" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Random Seed</label>
                <input type="number" defaultValue={42819} className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-white font-mono focus:outline-none focus:border-indigo-500 transition-colors" />
              </div>
            </div>

            <div className="pt-6 mt-6 border-t border-slate-800">
              <button className="w-full py-4 bg-rose-600 hover:bg-rose-500 shadow-lg shadow-rose-500/20 text-white rounded-xl font-bold transition-all flex items-center justify-center text-lg">
                <ShieldAlert className="mr-2" />
                INJECT FAILURE
              </button>
            </div>
          </div>
        </div>

        {/* Active Faults Panel */}
        <div className="col-span-1 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl flex flex-col">
          <h2 className="text-lg font-bold text-white flex items-center mb-6 pb-4 border-b border-slate-800">
            <Activity size={18} className="mr-2 text-emerald-400" /> Active Faults
          </h2>

          <div className="space-y-4 flex-1">
            {activeFaults.map(fault => (
              <div key={fault.id} className="bg-slate-950 border border-slate-800 rounded-xl p-4 relative overflow-hidden">
                <div className="absolute top-0 left-0 w-1 h-full bg-rose-500"></div>
                
                <div className="flex justify-between items-start mb-2">
                  <div className="font-mono text-xs font-bold text-slate-300">{fault.id}</div>
                  <div className="text-[10px] px-2 py-0.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-full font-bold">
                    {fault.status}
                  </div>
                </div>

                <div className="text-sm font-bold text-rose-400 mb-1">{fault.type}</div>
                <div className="text-xs text-slate-400 mb-2">{fault.service}</div>
                
                <div className="flex space-x-2 mb-4">
                  <span className="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded uppercase">{fault.mode}</span>
                  <span className="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded font-mono">100% PROB</span>
                </div>

                <button className="w-full py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-xs font-bold transition-colors flex items-center justify-center">
                  <Power size={12} className="mr-1 text-rose-400" /> DISABLE
                </button>
              </div>
            ))}
            
            {activeFaults.length === 0 && (
              <div className="text-center text-slate-500 mt-10">
                <CheckCircle className="mx-auto mb-2 opacity-50" size={24} />
                <p className="text-sm">No active faults.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
